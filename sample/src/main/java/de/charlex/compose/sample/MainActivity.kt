package de.charlex.compose.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import de.charlex.compose.sample.ui.theme.*

data class Item(
    val label: String,
    val color: Color,
    val directions: Set<RevealDirection>,
    val closeOnClick: Boolean = true
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RevealSwipeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                    contentColor = colors.onPrimary
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {

                        RevealSamples(
                            items = listOf(
                                Item(
                                    label = "Both directions",
                                    color = colors.one,
                                    directions = setOf(
                                        RevealDirection.StartToEnd,
                                        RevealDirection.EndToStart,
                                    ),
                                ),
                                Item(
                                    label = "Both directions, closeOnClick = false",
                                    color = colors.two,
                                    directions = setOf(
                                        RevealDirection.StartToEnd,
                                        RevealDirection.EndToStart,
                                    ),
                                    closeOnClick = false,
                                ),
                                Item(
                                    label = "StartToEnd",
                                    color = colors.three,
                                    directions = setOf(
                                        RevealDirection.StartToEnd,
                                    ),
                                ),
                                Item(
                                    label = "EndToStart",
                                    color = colors.four,
                                    directions = setOf(
                                        RevealDirection.EndToStart,
                                    ),
                                )
                            )
                        )

                        ComplexRevealSamples()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RevealSamples(items: List<Item>) {
    Column() {
        items.forEach { item ->
            RevealSwipe(
                modifier = Modifier.padding(vertical = 5.dp),
                directions = item.directions,
                hiddenContentStart = {
                    Star()
                },
                hiddenContentEnd = {
                    Trash()
                },
                closeOnContentClick = item.closeOnClick
            ) {
                Card(
                    backgroundColor = item.color,
                    shape = it,
                ){
                    Box(
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 20.dp),
                            text = item.label
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComplexRevealSamples() {
    Column() {
        TextFieldRevealSwipe()
        ButtonRevealSwipe()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ButtonRevealSwipe() {
    RevealSwipe(
        modifier = Modifier.padding(vertical = 5.dp),
        hiddenContentStart = {
            Star()
        },
        hiddenContentEnd = {
            Trash()
        },
    ) {
        Card(
            shape = it,
            backgroundColor = colors.two
        ) {
            Row(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Spacer(modifier = Modifier.width(16.dp))
                val state = remember { mutableStateOf(false) }
                Button(onClick = { state.value = !state.value }) {
                    Text("Click me!")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(if (state.value) "Clicked!" else "")
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TextFieldRevealSwipe() {
    RevealSwipe(
        modifier = Modifier.padding(vertical = 5.dp),
        hiddenContentStart = {
            Star()
        },
        hiddenContentEnd = {
            Trash()
        },
//        contentClickHandledExtern = true
    ) {
        Card(
            shape = it,
        ) {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {

                val text = remember { mutableStateOf("") }
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    value = text.value,
                    onValueChange = { text.value = it },
                    decorationBox = {
                        it()
                        if (text.value.isBlank()) {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                                Text("Enter Name")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Trash() {
    Icon(
        modifier = Modifier.padding(horizontal = 25.dp),
        imageVector = Icons.Outlined.Delete,
        contentDescription = null
    )
}

@Composable
private fun Star() {
    Icon(
        modifier = Modifier.padding(horizontal = 25.dp),
        imageVector = Icons.Outlined.Star,
        contentDescription = null,
        tint = Color.White
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RevealSwipeTheme {
        RevealSamples(listOf(
            Item(
                label = "Both directions",
                color = Color.DarkGray,
                directions = setOf(
                    RevealDirection.StartToEnd,
                    RevealDirection.EndToStart,
                ),
            )
        ))
    }
}