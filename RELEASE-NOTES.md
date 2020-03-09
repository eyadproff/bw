# Release Notes:

| prefix |         description          |
|:------ |:-----------------------------|
| FIX    | bug fixes note               |
| ENH    | enhancement on existing code |
| NEW    | new feature added            |
| CHN    | change functionality         |

VERSION SCHEMA: vYYYY.MM.#SEQ

---

## v2020.03.1

- NEW: Miscreant Fingerprints Enrollment Workflow.
- FIX: Fix bug in crime types lookup.

---

## v2020.01.2

- NEW: Civil Fingerprints Inquiry Workflow.

---

## v2020.01.1

- NEW: Criminal Transactions Inquiry Workflow.
- NEW: Iris Registration.
- NEW: Iris Inquiry.
- NEW: Add Palm-Fingerprint-Capturing in criminal workflows.

---

## v2019.11.2

- CHN: Don't send slaps if segmented fingerprints are available.

---

## v2019.11.1

- CHN: Use facePhoto that is extracted from NIST file when the inquiry result has no facePhoto.
- CHN: Don't depend on missing fingerprints count coming from different sources (e.g. NIST file). Get it from ShowingFingerprintsPaneFxController.

---

## v2019.09.2

- FIX: The slap thumbs overwrites segmented thumbs.

---

## v2019.09.1

- FIX: The slap fingerprints are segmented even though there are segmented fingerprints available from the source.
- ENH: Upgrade Retrofit from v2.5.0 to v2.6.1.
- ENH: Upgrade tomcat-websocket from v8.5.33 to v9.0.24.

---

## v2019.08.3

- FIX: NPE while building fingerprint inquiry report if PersonInfo is null.

---

## v2019.08.2

- NEW: NIST file generator.

---

## v2019.08.1

- CHN: If the returned SAMIS ID of DeporteeInfo is 0, set it to deportee ID.
- FIX: NPE if the fingerprint returned from the middleware is null.
- CHN: Show Naturalized Saudi labels.
- NEW: Fingerprint inquiry report.
- FIX: When data retrieved by criminal-hit only, the data does not persist upon navigate forward and backward.

---

## v2019.07.4

- ENH: Make all workflow threads as daemon threads.
- FIX: Open core.beans to gson in order to make BW runs on JDK12+.
- FIX: Fix issue when selecting server in DEV environment.

---

## v2019.07.3

- FIX: Fix a bug in interrupting threads on closing multiple tabs.

---

## v2019.07.2

- NEW: Add multiple tabs feature that supports multiple workflows at the same time.

---

## v2019.07.1

- ENH: Add the ability to configure custom server.
- FIX: Fix the deploy task in gradle.
- FIX: Fix NPE in CancelLatentWorkflow.

---

## v2019.05.3

- NEW: Add Delete-Criminal-Fingerprints workflow.
- CHN: Allow skipping 9 fingerprints in criminal workflows without role.

---

## v2019.05.2

- NEW: Add Register-Criminal-Fingerprints-For-Present-Person workflow.
- NEW: Add Register-Criminal-Fingerprints-For-Not-Present-Person workflow.

---

## v2019.05.1

- FIX: Using civil biometrics ID and criminal biometrics ID in register-convicted-report-for-non-present workflow is not complete.
- FIX: Spaces are allowed in text fields and are not trimmed.

---

## v2019.04.3

- FIX: Disable update-personId-info on Convicted-Report-Registration on going back.

---

## v2019.04.2

- CHN: Disable updating personId when editing convicted report.
- FIX: when navigating between convicted reports history, if the new value is null it will not override the old field value.

---

## v2019.04.1

- ENH: Unify the transition overlay for menu transition and wizard transition.
- ENH: Upgrade controlsfx library from 9.0.0 to 11.0.0.
- NEW: Add Edit-Convicted-Report workflow.
- CHN: Add 2 more options as fingerprints source: byCivilBiometricsId and byCriminalBiometricsId.
- CHN: Add more criteria in search-convicted-report-by-search-criteria workflow.
- CHN: Add history of all previous copies in convicted-report-dialog.

---

## v2019.03.5

- FIX: Fix Delete-Complete-Criminal-Record workflow logic.
- CHN: Send segmented and slap fingerprints when registering criminal fingerprints.

---

## v2019.03.4

- FIX: logging-interceptor-*.jar and okhttp-*.jar cannot be packed by java 6.

---

## v2019.03.3

- ENH: Combine strings and errors in single resource bundle.
- ENH: Update JavaFX to 12.
- ENH: Update ScenicView to 11.0.2.
- ENH: Complete Delete-Convicted-Report and Delete-Complete-Criminal-Record workflows.

---

## v2019.03.2

- ENH: Add a small button besides some labels to be able to copy the text.

---

## v2019.03.1

- ENH: Upgrading biokit-library to v2019.03.1.
- ENH: Print HTTP method and HTTP request body on the console.
- NEW: Add capturing fingerprints via fingerprint scanner as fingerprints source of civilcriminalfingerprintsinquiry workflow.
- FIX: CheckForNewUpdatesWorkflowTask NullPointerException.
- FIX: CancelCriminalWorkflow NumberFormatException.
- CHN: Add the ability to upload face photo in convicted-report workflows.
- CHN: Enable registering unknown criminal in case of no hit.

---

## v2019.02.2

- CHN: Add semat-ssl-certificate to the embedded keystore.

---

## v2019.02.1

- FIX: Crime codes were not being sent if not sharable.
- FIX: Nationalities without MOFA code had suffix ().

---

## v2019.01.7

- ENH: Add URL in error details upon webservice timeout.
- CHN: Remove path-params from all URLs.
- NEW: Integrate with deportee webservice for getting his information on fingerprints inquiry.
- CHN: Include nationalities without MOFA code.

---

## v2019.01.6

- NEW: Report client error to the server.

---

## v2019.01.5

- ENH: Enhance information exchange page.
- FIX: Fix the regression in RegisteringConvictedReportPaneFxController.

---

## v2019.01.4

- CHN: Add REGISTRANT_OPERATOR_ID and INQUIRER_OPERATOR_ID to the convicted report.
- CHN: Remove CrimeEvent from the convicted report.
- NEW: Add "Clear fields" button in Convicted-Report-Inquiry-By-Search-Criteria workflow.
- ENH: Upgrade gradle to 5.1.1.
- FIX: Disable wrapping top menu titles.

---

## v2019.01.3

- NEW: Add Delete-Convicted-Report workflow.
- NEW: Add Delete-Complete-Criminal-Record workflow.
- NEW: Add WEBSERVICES-LIST.md.
- CHN: Change the workflow fingerprintsinquiry to civilcriminalfingerprintsinquiry.
- NEW: Add shortcut CTRL+SHIFT+ALT+A to open the application folder.
- CHN: Add new step for submitting convicted reports.
- CHN: Add requirement warning before sharing convicted information.
- ENH: Update bio-commons to v2019.01.2.

---

## v2019.01.2

- ADD: Remove Upload-Image step from Security-Clearance-Face-Verification workflow.

---

## v2019.01.1

- ADD: Add Security-Clearance-Face-Verification.

---

## v2018.12.6

- FIX: Show confirmation button on register-convicted.

---

## v2018.12.5

- NEW: Add an option to restart the application on exit.
- FIX: Prevent adding more than one wizard pane in rare cases.
- FIX: Prevent blocking the application navigation in case one the of the lookups takes long time.
- ENH: In case of invalid state in workflows, navigate the user to the home page and set error label.
- ENH: Upgrade ScenicView.
- CHN: Update the fingerprint inquiry results to include records from DIS.
- CHN: Change the minimum application resolution from 800x600 to 1024x768.
- NEW: Add Fingerprints-Inquiry workflow.
- FIX: Fix a bug in ConvictedReportToPersonInfoConverter where identityInfo is not set.
- CHN: Add Semat copyrights in convicted report.

---

## v2018.12.4

- NEW: Add the ability to change the server URL at runtime on DEV and LOCAL environments.
- ENH: Change shortcuts for opening ScenicView to CTRL+SHIFT+ALT+D and assign CTRL+SHIFT+ALT+S to change the server URL.
- NEW: Add Convicted-Report-Inquiry-By-Search-Criteria workflow.
- ENH: Enhance the DatePicker UX.
- CHN: Remove convictedreportinquiry workflow.

---

## v2018.12.3

- FIX: Assign SAMIS ID to PersonID instead of idNumber.

---

## v2018.12.2

- FIX: Fix Search-By-Image workflow.
- FIX: Fix Convicted-Report-For-Not-Present-Person workflow.

---

## v2018.12.1

- ENH: Improve the wizard slider to support unlimited number of items.
- ENH: Improve lookups across the workflows.
- ENH: Improve the workflow engine.
- ENH: Improve the menu navigation.
- ENH: Update biokit-library jar to v2018.09.1.
- ENH: Improve the login transition and eliminate the ui lagging.
- ENH: Upgrade from Java 8 to Java 11.
- ENH: Split the project into java modules.
- ENH: Add the ability to mock tasks.
- NEW: Embed ScenicView with BW. It opens by the shortcut CTRL+SHIFT+ALT+S.
- ENH: Update Retrofit library to 2.5.0.
- NEW: Integrate with the activity services.
- ENH: Upgrade gradle to 5.0.
- NEW: Add Upload-NIST-File in Convicted-Report-Not-Present workflow.

---

## v2018.09.4

- FIX: close the stream after saving the pdf file.
- CHN: integrate SAMIS id, SAMIS id type and bio id into the following workflows: "convicted-report", "dead-person-report", "fingerprints-card-identification".
- FIX: fix a bug in "delink criminal" workflow upon submitting the request. Also fix the style.

---

## v2018.09.3

- NEW: Convicted Report Inquiry.
- ENH: remove any name part that equals to '-'.
- ENH: make all number-labels to show arabic numbers in case of choosing Arabic language.
- FIX: change the reports font from 'Arial' to 'Times New Roman'.
- ENH: improve the workflow engine.

---

## v2018.09.2

- FIX: make the check-mark symbol appears in convicted report.
- ENH: make the login box wider to appear correctly on small screens.

---

## v2018.09.1

- FIX: add jasper-compiler-jdt jar to the libraries which provides javac at runtime.
- CHN: make arrest-date field optional.

---

## v2018.08.8

- Upgrade libraries to the latest version.
- Remove unnecessary dependencies and reduce size of the final packages to the minimum.
- Upgrade gradle to 4.10.
- Upgrade JUnit to 5.2.
- Make webservice urls hardcoded into retrofit interfaces.
- Migrate app configurations to the database.

## v2018.08.7

- Set id number to be samis id in case the former is null.

## v2018.08.6

- Bug fixes in convicted report workflow and fingerprint card identification workflow.

## v2018.08.5

- Fingerprint Card Identification: set samis id to documentId in case of personInfo is null.

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