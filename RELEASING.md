Take the following steps to release a new version of the library to Bintray (jCenter):

1. Run all unit tests.
2. Make sure all your changes are committed and pushed to the origin repository.
3. From the terminal, run the Maven Release plugin, as follows:
    a. `mvn release:prepare` (note: you will be asked which version you are releasing)
    b. `mvn release:perform`
4. Publish the uploaded artifacts on Bintray:
    a. Visit the project on [Bintray](https://bintray.com/cookingfox/maven/chefling-di-java).
    b. Click the "Publish" link for the notification that says "You have X unpublished items".
    c. Publish to Maven Central by clicking the "Maven Central" link.
5. Describe the changes of this release in the [CHANGELOG](../CHANGELOG.md).
6. Update the version number in the [README](../README.md).
7. Push changes to GitHub.
8. Add release notes on GitHub, which is typically the same as the CHANGELOG additions.
