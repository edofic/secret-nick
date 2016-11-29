# Secret Nick

A web application for hosting a Secret Santa event.  Login with Facebook and
provide your wish + signature. At some point admin shuffles the wishes and then
you can read a letter from someone else. You then provide a present and direct
it to that pseudonym in hopes of making someone happy.

## Development

Easies is to use provided nix shell by running `nix-shell` at the project root.
Then run `sbt run`. You need to provide your own Postgres. Development settings
can be exported (bash export) in `DEV_SETTINGS` at project root - this file is
sourced when you start `nix-shell.` See *deployment* for available settings.

## Deployment

App deploys to Heroku without any modifications. It only requires you to create
a Facebook app to enable login with Facebook.

Needed config variables:
- `APPLICATION_SECRET` private key to encrypt play sessions
- `DATABASE_URL` Postgres provided by Heroku
- `FB_APP_ID` Facebook application id
- `FB_APP_SECRET` Facebook application secret
- `SHUFFLE_SECRET` Protection for triggering, see below

## Shuffling

Once the admin decides its time to shuffle the wishes he issues an HTTP POST
request to `/shuffle` providing `secret` in the query string. This value should
match `SHUFFLE_SECRET` in the deployment settings. This is a protection
mechanism so nobody else could trigger a shuffle. Shuffle is **not**
idempotent.
