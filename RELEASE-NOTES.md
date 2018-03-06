# Release Notes:

## v2018.03.1

- Add "MOFA Enrollment" feature.

## v2018.02.1

- Source code overhaul.
- Replace the workflow engine "Activiti" with a custom solution.
- Improve error messages and fix some.
- Build workflow wizard.
- Fixed the login credentials on Dev environment.
- Build auto-complete combo-box.
- Show confirmation dialog on exit.
- Logout before exiting.
- Make Sunday as first day of the week when the language is Arabic.
- Apply "application" gradle plugin when running in development environment.
- Cancel sending the IP address from the client side.
- Add support for BCL v1.1.
- Package any java-6 compatible JARs as .pack.gz.
- Use BCL-Utils to check for new updates.

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
- Make the change-password-thread daemon.
- Start the application with the OS's language in the first run and save the language in user preferences on switching for subsequent launches of the application.
- Improve the error logging.
- Set the primary stage as owner for all dialogs.

## v1.1.2

- Support launching by BCL.

## v1.1.1

- Fix some bugs in "Search-By-Face" feature.

## v1.1.0

- Add "Search-By-Face" feature.

## v1.0.0

- Initial Release.