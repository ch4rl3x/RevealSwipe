package de.charlex.compose.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import de.charlex.compose.rememberRevealState

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
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
                                    color = MaterialTheme.colorScheme.primary,
                                    directions = setOf(
                                        RevealDirection.StartToEnd,
                                        RevealDirection.EndToStart,
                                    ),
                                ),
                                Item(
                                    label = "Both directions, closeOnClick = false",
                                    color = MaterialTheme.colorScheme.secondary,
                                    directions = setOf(
                                        RevealDirection.StartToEnd,
                                        RevealDirection.EndToStart,
                                    ),
                                    closeOnClick = false,
                                ),
                                Item(
                                    label = "StartToEnd",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    directions = setOf(
                                        RevealDirection.StartToEnd,
                                    ),
                                ),
                                Item(
                                    label = "EndToStart",
                                    color = MaterialTheme.colorScheme.primary,
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

@Composable
fun RevealSamples(items: List<Item>) {
    Column() {
        items.forEach { item ->
            RevealSwipe(
                modifier = Modifier.padding(vertical = 5.dp),
                state = rememberRevealState(directions = item.directions),
                hiddenContentStart = {
                    Star()
                },
                hiddenContentEnd = {
                    Trash()
                },
                backgroundStartActionLabel = "Mark entry as favorite",
                backgroundEndActionLabel = "Delete entry",
                closeOnContentClick = item.closeOnClick
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = item.color
                    ),
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

@Composable
fun ComplexRevealSamples() {
    Column() {
        TextFieldRevealSwipe()
        ButtonRevealSwipe()
        ContentClickRevealSwipe()
    }
}

@Composable
private fun ButtonRevealSwipe() {
    RevealSwipe(
        onContentClick = null,
        modifier = Modifier.padding(vertical = 5.dp),
        hiddenContentStart = {
            Star()
        },
        hiddenContentEnd = {
            Trash()
        },
        backgroundStartActionLabel = "Mark entry as favorite",
        backgroundEndActionLabel = "Delete entry"
    ) {
        Card(
            shape = it,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Row(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Spacer(modifier = Modifier.width(16.dp))
                val state = remember { mutableStateOf(false) }
                Text("onContentClick = null")
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { state.value = !state.value }) {
                    Text("Click me!")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(if (state.value) "Clicked!" else "")
            }
        }
    }
}

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
        backgroundStartActionLabel = "Mark entry as favorite",
        backgroundEndActionLabel = "Delete entry"
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
                            Text("Enter Name")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ContentClickRevealSwipe() {
    val state = remember { mutableStateOf(false) }
    RevealSwipe(
        onContentClick = { state.value = !state.value },
        closeOnContentClick = true,
        modifier = Modifier.padding(vertical = 5.dp),
        hiddenContentStart = {
            Star()
        },
        hiddenContentEnd = {
            Trash()
        },
        backgroundStartActionLabel = "Mark entry as favorite",
        backgroundEndActionLabel = "Delete entry"
    ) {
        Card(
            shape = it,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Row(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Spacer(modifier = Modifier.width(16.dp))
                Text("Click me!")
                Spacer(modifier = Modifier.width(16.dp))
                Text(if (state.value) "Clicked!" else "")
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
    MaterialTheme {
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