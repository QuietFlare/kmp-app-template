#!/usr/bin/env python3
"""Turn a fresh copy of the template into a named app.

    python3 tools/bootstrap.py --name Lantern --slug lantern --bundle-id com.acme.lantern --repo acme/lantern
    python3 tools/bootstrap.py --name Lantern --slug lantern --bundle-id com.acme.lantern --repo acme/lantern --dry-run

Rewrites every identity string and renames the files and directories that carry them:

    Starter                       -> --name        display name, Swift/Kotlin type prefixes
    starter                       -> --slug        namespaces, artifact names, URLs, keys
    com.example.starter           -> --bundle-id   applicationId + iOS bundle id
    OWNER/REPO                    -> --repo        GitHub owner/name
    example.com/starter           -> --site        marketing/privacy/support base (default github.com/<repo>)

Longer, more specific strings are replaced before bare words, so `com.example.starter`
becomes the bundle id rather than `com.example.<slug>`. GitHub's "Use this template" copies
files verbatim; this script is the substitution step. Run it once, review the diff, delete it, commit.
"""
import argparse
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

SKIP_DIRS = {".git", ".gradle", ".kotlin", "build", "DerivedData", "node_modules", "__pycache__",
             "xcuserdata"}
BINARY_EXT = {".png", ".jpg", ".jpeg", ".gif", ".ico", ".jar", ".mov", ".mp4", ".m4a", ".wav",
              ".ttf", ".otf", ".zip", ".aab", ".apk", ".jks", ".p8", ".p12", ".pyc"}


def parse():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("--name", required=True, help="Display name, e.g. Lantern (letters only, capitalised)")
    p.add_argument("--slug", required=True, help="Lowercase identifier, e.g. lantern (a-z0-9 only)")
    p.add_argument("--bundle-id", required=True, help="applicationId / iOS bundle id, e.g. com.acme.lantern")
    p.add_argument("--repo", required=True, help="GitHub owner/name, e.g. acme/lantern")
    p.add_argument("--site", help="Site base without scheme; default github.com/<repo>")
    p.add_argument("--dry-run", action="store_true", help="Report what would change; write nothing")
    a = p.parse_args()
    if not re.fullmatch(r"[A-Z][A-Za-z0-9]*", a.name):
        sys.exit("--name must be a capitalised identifier with no spaces (it becomes a Swift/Kotlin type prefix)")
    if not re.fullmatch(r"[a-z][a-z0-9]*", a.slug):
        sys.exit("--slug must be lowercase letters/digits only (it becomes a Kotlin package segment)")
    if not re.fullmatch(r"[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)+", a.bundle_id):
        sys.exit("--bundle-id must be reverse-DNS, lowercase, e.g. com.acme.lantern")
    a.site = (a.site or f"github.com/{a.repo}").replace("https://", "").rstrip("/")
    return a


def replacements(a):
    # Most specific first. Each entry: (old, new).
    return [
        ("com.example.starter", a.bundle_id),
        ("OWNER/REPO", a.repo),
        ("example.com/starter", a.site),
        ("STARTER", a.slug.upper()),
        ("Starter", a.name),
        ("starter", a.slug),
    ]


def walk_files():
    for dirpath, dirnames, filenames in os.walk(ROOT):
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
        for f in filenames:
            yield os.path.join(dirpath, f)


def rewrite_contents(a, dry):
    changed = []
    me = os.path.abspath(__file__)
    for path in walk_files():
        if path == me or os.path.splitext(path)[1].lower() in BINARY_EXT:
            continue
        try:
            with open(path, encoding="utf-8") as fh:
                text = fh.read()
        except (UnicodeDecodeError, OSError):
            continue
        new = text
        for old, rep in replacements(a):
            new = new.replace(old, rep)
        if new != text:
            changed.append(os.path.relpath(path, ROOT))
            if not dry:
                with open(path, "w", encoding="utf-8") as fh:
                    fh.write(new)
    return changed


def rename_paths(a, dry):
    """Rename files and directories whose names carry the placeholder, deepest first."""
    renamed = []
    entries = []
    for dirpath, dirnames, filenames in os.walk(ROOT):
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
        for n in dirnames + filenames:
            entries.append(os.path.join(dirpath, n))
    for path in sorted(entries, key=lambda p: -p.count(os.sep)):
        base = os.path.basename(path)
        new_base = base
        for old, rep in replacements(a):
            new_base = new_base.replace(old, rep)
        if new_base != base:
            dest = os.path.join(os.path.dirname(path), new_base)
            renamed.append((os.path.relpath(path, ROOT), os.path.relpath(dest, ROOT)))
            if not dry:
                os.rename(path, dest)
    return renamed


def main():
    a = parse()
    dry = a.dry_run
    print(f"{'Dry run: ' if dry else ''}bootstrapping {a.name} ({a.slug})")
    print(f"  bundle id : {a.bundle_id}")
    print(f"  repo      : {a.repo}")
    print(f"  site      : https://{a.site}")

    changed = rewrite_contents(a, dry)
    renamed = rename_paths(a, dry)

    print(f"\n{len(changed)} files rewritten:")
    for c in changed:
        print("  " + c)
    print(f"\n{len(renamed)} paths renamed:")
    for src, dst in renamed:
        print(f"  {src} -> {dst}")

    print("""
Next:
  1. Review the diff (git diff; git status for renames).
  2. Replace the placeholder palette in design/tokens.json, then: python3 design/tokens.py
  3. Replace draw_mark() in tools/render_icon.py and the paths in
     androidApp/src/main/res/drawable/ic_launcher_{foreground,monochrome}.xml, then:
     python3 tools/render_icon.py
  4. cd iosApp && xcodegen generate
  5. export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
     ./gradlew :shared:jvmTest :androidApp:testDebugUnitTest :androidApp:lintDebug
  6. Fill in the <!-- app-specific --> sections of CLAUDE.md and COMPLIANCE.md.
  7. git rm tools/bootstrap.py
""")
    return 0


if __name__ == "__main__":
    sys.exit(main())
