# RevealSwipe
Compose RevealSwipe

Swipable in both directions

<a href="https://github.com/ch4rl3x/RevealSwipe/actions?query=workflow%3ALint"><img src="https://github.com/ch4rl3x/RevealSwipe/workflows/Lint/badge.svg" alt="Lint"></a>
<a href="https://github.com/ch4rl3x/RevealSwipe/actions?query=workflow%3AKtlint"><img src="https://github.com/ch4rl3x/RevealSwipe/workflows/Ktlint/badge.svg" alt="Ktlint"></a>

<a href="https://www.codefactor.io/repository/github/ch4rl3x/RevealSwipe"><img src="https://www.codefactor.io/repository/github/ch4rl3x/RevealSwipe/badge" alt="CodeFactor" /></a>
<a href="https://repo1.maven.org/maven2/de/charlex/compose/revealswipe/"><img src="https://img.shields.io/maven-central/v/de.charlex.compose/revealswipe" alt="Maven Central" /></a>


# Add to your project

Add actual RevealSwipe library:

```groovy
dependencies {
    implementation 'de.charlex.compose:revealswipe:1.0.0-beta01'
}
```

# How does it work?

Surround your content with the RevealSwipe

```kotlin
RevealCard(
    modifier = Modifier.padding(vertical = 5.dp),
    backgroundCardModifier = Modifier.padding(vertical = 5.dp),
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

# Directions

### EndToStart
![RevealSwipe EndToStart](https://github.com/ch4rl3x/RevealSwipe/blob/main/art/revealswipe_end.gif)

### StartToEnd
![RevealSwipe StartToEnd](https://github.com/ch4rl3x/RevealSwipe/blob/main/art/revealswipe_start.gif)

### Both Directions
![RevealSwipe Both](https://github.com/ch4rl3x/RevealSwipe/blob/main/art/revealswipe.gif)



That's it!

License
--------

    Copyright 2020 Alexander Karkossa

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
