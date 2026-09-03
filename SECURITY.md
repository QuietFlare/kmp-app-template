# Security

For a security or privacy issue, please do not open a public issue. Use GitHub's private
reporting: **Security > Report a vulnerability** on this repository. You will get a reply,
and we will coordinate a fix with you before anything is published.

What is in scope: anything that could expose a user's data, weaken the on-device protections
(Keychain / Keystore `KeyVault`, backup exclusions, app lock), or let the app make a network
request it does not declare.

Secrets never live in this repository. The client holds no credentials; release signing and
store credentials arrive in CI as repository secrets and are shredded after use. If you find
something that looks like a secret in the history, report it the same way.
