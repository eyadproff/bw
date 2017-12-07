# Release Notes:

## v1.2.0

- Add avatar image of the operator.
- Checking for new updates on login.
- Fix a bug: set the avatar image to the default placeholder in case the operator has associated image.
- Fix a bug: fix the log rotation (rotate after 50MB).
- Replace socket locking with file locking as a means to prevent multiple instance of the application per user instead of per machine.
- Improve the core workflow.
- Fix a bug: call the logout webservice correctly.
- Restructure the packages by collection all the features inside a single package.
- Decentralize the menus.
- Move all configurable properties from config.properties to APPLICATION.JNLP that is used by BCL.
- Register all pop-up dialogs for idle monitoring.
- Improve the logging configuration.
- Allow the user to press ENTER from the keyboard to accept the confirmation dialog message.
- Show the environment type in the window title.
- Add "change-password" feature.

## v1.1.2

- Support launching by BCL.

## v1.1.1

- Fix some bugs in "Search-By-Face" feature.

## v1.1.0

- Add "Search-By-Face" feature.

## v1.0.0

- Initial Release.