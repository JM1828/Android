pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "My Application"
include(":app")
include(":app:viewapp")
include(":viewoption")
include(":ch6_view2")
include(":linearlayoutex")
include(":relativelayout")
include(":framelayout")
include(":gridlayout")
include(":constraintlayout")
include(":ch7_layout")
include(":userevent")
include(":aaaaa")
include(":ch8_event")
include(":ch9_resource")
include(":permissionex")
include(":alertex")
include(":ch10_notification")
include(":fragmentex")
include(":recyclerviewex")
include(":viewpager2ex")
include(":drawlayoutex")
include(":ch11_jetpack")
