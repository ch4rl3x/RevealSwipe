package de.charlex.compose.sample

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.charlex.compose.BaseRevealSwipe
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
        val context = LocalContext.current
        items.forEach { item ->
            RevealSwipe(
                modifier = Modifier.padding(vertical = 5.dp),
                state = rememberRevealState(directions = item.directions),
                hiddenContentStart = {
                    SText()
                },
                hiddenContentEnd = {
                    TText()
                },
                backgroundStartActionLabel = "Mark entry as favorite",
                backgroundEndActionLabel = "Delete entry",
                closeOnContentClick = item.closeOnClick,
                onContentClick = {
                    Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show()
                },
                onContentLongClick = { offset ->
                    Toast.makeText(context, "LongClick: $offset", Toast.LENGTH_SHORT).show()
                },
                onBackgroundEndClick = {
                    Toast.makeText(context, "End", Toast.LENGTH_SHORT).show()
                    true
                }, onBackgroundStartClick = {
                    Toast.makeText(context, "Start", Toast.LENGTH_SHORT).show()
                    true
                },
                backgroundCardEndColor = MaterialTheme.colorScheme.secondaryContainer,
                backgroundCardStartColor = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.medium,
                card = { shape, content ->
                    Card(
                        modifier = Modifier.matchParentSize(),
                        colors = CardDefaults.cardColors(
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            containerColor = Color.Transparent
                        ),
                        shape = shape,
                        content = content
                    )
                }
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
        BaseRevealSwipe()
        TextFieldRevealSwipe()
        ButtonRevealSwipe()
        ButtonRevealSwipe2()
        ContentClickRevealSwipe()
    }
}

@Composable
private fun BaseRevealSwipe() {
    val context = LocalContext.current

    BaseRevealSwipe(
        modifier = Modifier.padding(vertical = 5.dp),
        hiddenContentEnd = {
            Row(
//                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    onClick = {
                        Toast.makeText(context, "S", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    SText()
                }
                IconButton(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    onClick = {
                        Toast.makeText(context, "T", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    TText()
                }
            }
        },
        state = rememberRevealState(
            maxRevealDp = 150.dp,
            directions = setOf(
                RevealDirection.EndToStart,
            )
        ),
        backgroundCardEndColor = MaterialTheme.colorScheme.secondaryContainer,
        backgroundCardStartColor = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        card = { shape, content ->
            Card(
                modifier = Modifier.matchParentSize(),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = Color.Transparent
                ),
                shape = shape,
                content = content
            )
        }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = CardDefaults.elevatedCardElevation(),
            shape = it
        ) {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = "BaseRevealSwipe"
                )
            }
        }
    }
}

@Composable
private fun ButtonRevealSwipe() {
    RevealSwipe(
        onContentClick = null,
        modifier = Modifier.padding(vertical = 5.dp),
        hiddenContentStart = {
            SText()
        },
        hiddenContentEnd = {
            TText()
        },
        backgroundStartActionLabel = "Mark entry as favorite",
        backgroundEndActionLabel = "Delete entry",
        backgroundCardEndColor = MaterialTheme.colorScheme.secondaryContainer,
        backgroundCardStartColor = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        card = { shape, content ->
            Card(
                modifier = Modifier.matchParentSize(),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = Color.Transparent
                ),
                shape = shape,
                content = content
            )
        }
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
private fun ButtonRevealSwipe2() {
    RevealSwipe(
        onContentClick = {

        },
        modifier = Modifier.padding(vertical = 5.dp),
        hiddenContentStart = {
            SText()
        },
        hiddenContentEnd = {
            TText()
        },
        backgroundStartActionLabel = "Mark entry as favorite",
        backgroundEndActionLabel = "Delete entry",
        backgroundCardEndColor = MaterialTheme.colorScheme.secondaryContainer,
        backgroundCardStartColor = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        card = { shape, content ->
            Card(
                modifier = Modifier.matchParentSize(),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = Color.Transparent
                ),
                shape = shape,
                content = content
            )
        }
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
                Text("onContentClick = { }")
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
            SText()
        },
        hiddenContentEnd = {
            TText()
        },
        backgroundStartActionLabel = "Mark entry as favorite",
        backgroundEndActionLabel = "Delete entry",
        backgroundCardEndColor = MaterialTheme.colorScheme.secondaryContainer,
        backgroundCardStartColor = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        card = { shape, content ->
            Card(
                modifier = Modifier.matchParentSize(),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = Color.Transparent
                ),
                shape = shape,
                content = content
            )
        }
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
            SText()
        },
        hiddenContentEnd = {
            TText()
        },
        backgroundStartActionLabel = "Mark entry as favorite",
        backgroundEndActionLabel = "Delete entry",
        backgroundCardEndColor = MaterialTheme.colorScheme.secondaryContainer,
        backgroundCardStartColor = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        card = { shape, content ->
            Card(
                modifier = Modifier.matchParentSize(),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = Color.Transparent
                ),
                shape = shape,
                content = content
            )
        }
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
private fun TText() {
    Text(
        modifier = Modifier.padding(horizontal = 25.dp),
        text = "T"
    )
}

@Composable
private fun SText() {
    Text(
        modifier = Modifier.padding(horizontal = 25.dp),
        text = "S"
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