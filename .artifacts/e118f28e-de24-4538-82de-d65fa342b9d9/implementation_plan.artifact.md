# Build Time & App Size Optimization Plan

The current project can be optimized for both faster builds and a smaller app size. The main issues are memory constraints in Gradle, inefficient dependency usage, and disabled code shrinking.

## User Review Required

> [!IMPORTANT]
> **Removing Material Icons Extended**: I am proposing to remove the `material-icons-extended` library. This library is very large and significantly slows down builds and increases APK size. I will replace the few icons used from it with local vector assets or standard icons to keep the app working perfectly.

## Proposed Changes

### Build System & Performance

#### [MODIFY] [gradle.properties](file:///C:/androifff/gradle.properties)
- Increase Gradle Heap size from 2GB to 4GB.
- Enable Parallel execution.
- Enable Build Cache and Configuration Cache.

### App Size Reduction (R8)

#### [MODIFY] [app/build.gradle.kts](file:///C:/androifff/app/build.gradle.kts)
- Enable `isMinifyEnabled` for the release build.
- Enable `isShrinkResources` for the release build.
- Remove `libs.compose.material.icons.extended`.

### Dependency Cleaning

#### [MODIFY] [Compose Screens]
- Update imports to use standard icons where possible.
- If specific "extended" icons are needed, I will add them as local XML files instead of pulling in the entire library.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleRelease` to ensure the app builds with minification enabled.
- Check for any `ClassNotFoundException` in logs which might indicate missing R8 keep rules (unlikely for this simple app but good to verify).

### Manual Verification
- Compare APK size before and after.
- Verify all icons in the UI are still visible.
