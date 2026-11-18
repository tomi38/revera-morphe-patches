# Revera Morphe Patches

Community [Morphe](https://github.com/MorpheApp) patches for [Revera](https://github.com/caruno-git/Revera), the open-source Android Spotify client.

Based on the structure of [myspoti-morphe-patches](https://github.com/amit-0i/myspoti-morphe-patches).

## Usage

In Morphe Manager, add this repository as a patch source:

```
https://github.com/tomi38/revera-morphe-patches
```

Or download the latest `patches-*.mpp` from [Releases](https://github.com/tomi38/revera-morphe-patches/releases) and load it manually.

Supported packages: `app.revera.dev` (GitHub Releases builds) and `app.revera`.

## Patches

<!-- PATCHES_START EXPANDED -->
> **[v1.0.0](https://github.com/tomi38/revera-morphe-patches/releases/tag/v1.0.0)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;2 patches total
<details open>
<summary>📦 Revera&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Spotify branding](#spotify-branding) | Renames Revera to Spotify and replaces the launcher (all color variants), themed, splash, widget and notification icons with the Spotify mark. |  |

</details>

<details open>
<summary>📦 Revera&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Spotify branding](#spotify-branding) | Renames Revera to Spotify and replaces the launcher (all color variants), themed, splash, widget and notification icons with the Spotify mark. |  |

</details>

<!-- PATCHES_END -->

## Building

The Morphe Gradle plugin is hosted on GitHub Packages, so a token with `read:packages` is required:

```sh
GITHUB_ACTOR=<user> GITHUB_TOKEN=<token> ./gradlew :patches:buildAndroid
```

Releases are created automatically by semantic-release from Conventional Commits on `main` (`feat:` → minor, `fix:` → patch).

## License

GPLv3. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
