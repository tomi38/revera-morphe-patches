group = "app.revera.patches"

patches {
    about {
        name = "Revera Morphe Patches"
        description = "Community patches for Revera: Spotify branding (app name and icons)."
        source = "https://github.com/caruno-git/Revera"
        author = "Revera Morphe Patches contributors"
        contact = "https://github.com/caruno-git/Revera/issues"
        website = "https://github.com/caruno-git/Revera"
        license = "GPLv3"
    }
}

// Separate configuration so gson is available at runtime for the
// generatePatchesList task but never bundled into the APK.
val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"
        dependsOn(build)
        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }
}
