<a href="https://github.com/ch4rl3x/RevealSwipe/actions?query=workflow%3ABuild and test"><img src="https://github.com/ch4rl3x/RevealSwipe/workflows/Build and test/badge.svg" alt="Build and test"></a>
<a href="https://github.com/ch4rl3x/RevealSwipe/actions?query=workflow%3ALint"><img src="https://github.com/ch4rl3x/RevealSwipe/workflows/Lint/badge.svg" alt="Lint"></a>
<a href="https://github.com/ch4rl3x/RevealSwipe/actions?query=workflow%3ASpotless"><img src="https://github.com/ch4rl3x/RevealSwipe/workflows/Spotless/badge.svg" alt="Spotless"></a>
<a href="https://www.codefactor.io/repository/github/ch4rl3x/RevealSwipe"><img src="https://www.codefactor.io/repository/github/ch4rl3x/RevealSwipe/badge" alt="CodeFactor" /></a>
<a href="https://repo1.maven.org/maven2/de/charlex/compose/revealswipe/"><img src="https://img.shields.io/maven-central/v/de.charlex.compose/revealswipe" alt="Maven Central" /></a>

# RevealSwipe

`RevealSwipe` is a Compose UI component library built for swipe-to-reveal interactions. With this library, you can wrap content and reveal hidden composables (e.g. action icons) when swiping left or right.

> [!NOTE]  
> 🚀 RevealSwipe is now Compose Multiplatform

🧩 What it does

* You can configure swipe directions (start → end, end → start).
* For each direction, you can supply hidden content (e.g. an icon, button, or any composable).
* The main content (your card, list item, etc.) slides over to reveal the hidden content beneath.
* You remain in control over layout, styling, and when/if the reveal should trigger for a full swipe or partial swipe.
* Works natively with Material 3 styling and theming.


## Dependency

Add actual RevealSwipe library:

```groovy
dependencies {
    implementation 'de.charlex.compose:revealswipe:3.1.0-rc01'
}
```

## How does it work?

Surround your content with the RevealSwipe

```kotlin
RevealSwipe(
    modifier = Modifier.padding(vertical = 5.dp),
    directions = setOf(
//        RevealDirection.StartToEnd,
        RevealDirection.EndToStart
    ),
    hiddenContentStart = {
        Icon(
            modifier = Modifier.padding(horizontal = 25.dp),
            imageVector = Icons.Outlined.Star,
            contentDescription = null,
            tint = Color.White
        )
    },
    hiddenContentEnd = {
        Icon(
            modifier = Modifier.padding(horizontal = 25.dp),
            imageVector = Icons.Outlined.Delete,
            contentDescription = null
        )
    }
) {
    Card(
        modifier = Modifier.fillMaxSize().requiredHeight(80.dp),
        backgroundColor = Color(item.second),
        shape = it,
    ){
        Text(
            modifier = Modifier.padding(start = 20.dp, top = 20.dp),
            text = item.first
        )
    }
}
```

## Preview

![RevealSwipe](https://github.com/ch4rl3x/RevealSwipe/blob/main/art/revealswipe.gif)


License
--------

    Copyright 2021 Alexander Karkossa

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
