package de.charlex.compose.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
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
    //                                RevealDirection.EndToStart,
                                ),
                            ),
                            Item(
                                label = "EndToStart",
                                color = colors.four,
                                directions = setOf(
    //                                RevealDirection.StartToEnd,
                                    RevealDirection.EndToStart,
                                ),
                            )
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RevealSamples(items: List<Item>) {
    Column(
        modifier = Modifier.padding( 16.dp)
    ) {
        items.forEach { item ->
            RevealSwipe(
                modifier = Modifier.padding(vertical = 5.dp),
                directions = item.directions,
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