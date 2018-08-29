# Release Notes:

## v2018.08.8

- Upgrade libraries to the latest version.
- Remove unnecessary dependencies and reduce size of the final packages to the minimum.
- Upgrade gradle to 4.10.
- Upgrade JUnit to 5.2.
- Make webservice urls hardcoded into retrofit interfaces.

## v2018.08.7

- Set id number to be samis id in case the former is null.

## v2018.08.6

- Bug fixes in convicted report workflow and fingerprint card identification workflow.

## v2018.08.5

- Fingerprint Card Identification: set samis id to idNumber in case of personInfo is null.

## v2018.08.4

- Change BCL to v2018.08.4 in JNLP file.
- Remove code that writes png files for the fingerprints on the disk.
- Fingerprint Card Identification: prevent empty list of fingerprints to the backend.

## v2018.08.3

- Check if the system supports 3D graphics before using it.
- Fix a bug in date-picker's radio button for selecting Gregorian calendar, when loading old values.
- Convicted Report: Fix a bug for setting unknown nationality when loading old values.
- Add "Fingerprint Card Identification" feature.

## v2018.08.2

- Convicted Report: fix a bug in inquiry result.

## v2018.08.1

- Print Dead Person Record: change the record's length from 20 to 15 digits.
- Support single fingerprint devices.
- Show device controls if needed only, based on user roles.
- Make all date-pickers show Hijri calendar by default and the calendar input is selectable.
- Convicted Report: add 2 more crime types and change the gui for choosing to be dynamic.
- Convicted Report: replace 0's with blank spaces in final report.
- Convicted Report: fix a bug in inquiry result if the person is unknown.

## v2018.07.4

- Add "Print Dead Person Record" feature.
- Apply https.
- Convicted Report: disable editing any person information retrieved from the backend.

## v2018.07.3

- Fix a bug where two-thumbs slap were saved in both 11 and 12.

## v2018.07.2

- Add "Login By Fingerprint" feature.

## v2018.07.1

- Add "Face Verification" feature.

## v2018.06.1

- Add hijri date and the week day to any gregorian date.
- Convicted Report: Make "Judgment Number" and "Police File Number" 20 characters long instead of 10.
- Convicted Report: Make "Other" 150 characters long instead of 60.

## v2018.05.5

- Update biokit-library from v2018.05.1 to v2018.05.2.

## v2018.05.4

- Fix the datetime format by modifying the timezone information.

## v2018.05.3

- Add "Visa Applicants Enrollment".
- Update biokit-library to the new version that supports passport scanning.
- Replace MOI logo with NIC and Semat logos.

## v2018.05.2

- Fix a bug where we were sending wrong fingerprint segmentation coordinates.

## v2018.05.1

- Add "Convicted-Report" feature.
- Update bcl-utils and biokit-library.

## v2018.03.2

- Update bcl-utils-2018.03.1 to bcl-utils-2018.03.2, to avoid using JRE8's javaws.

## v2018.03.1

- Integrate with Biokit-library.
- Add new common screens for fingerprint and face capturing (fingerprint's not ready yet).
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