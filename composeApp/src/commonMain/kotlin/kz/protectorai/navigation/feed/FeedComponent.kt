package kz.protectorai.navigation.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.SupervisorJob
import kz.protectorai.CommonHardcode
import kz.protectorai.core.EMPTY_STRING
import kz.protectorai.core.Stateful
import kz.protectorai.core.coroutineScope
import kz.protectorai.data.ProtectoraiRepository
import kz.protectorai.navigation.Composite
import kz.protectorai.ui.icons.ProtectoraiIcons

private const val DATE_CHAR_LENGTH = 6

data class Video(
    val description: String,
    val url: String,
    val subtitle: String,
    val thumb: String,
    val title: String,
    val width: Int,
    val height: Int
)

val videos = CommonHardcode.wildcard {
    listOf(
        Video(
            description = "Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him, something snaps... and the rabbit ain't no bunny anymore! In the typical cartoon tradition he prepares the nasty rodents a comical revenge.\n\nLicensed under the Creative Commons Attribution license\nhttp://www.bigbuckbunny.org",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            subtitle = "By Blender Foundation",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
            title = "Big Buck Bunny",
            width = 768,
            height = 432
        ),
        Video(
            description = "The first Blender Open Movie from 2006",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            subtitle = "By Blender Foundation",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg",
            title = "Elephant Dream",
            width = 768,
            height = 432
        ),
        Video(
            description = "HBO GO now works with Chromecast -- the easiest way to enjoy online video on your TV. For when you want to settle into your Iron Throne to watch the latest episodes. For $35.\nLearn how to use Chromecast with HBO GO and more at google.com/chromecast.",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            subtitle = "By Google",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg",
            title = "For Bigger Blazes",
            width = 768,
            height = 432
        ),
        Video(
            description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV—for when Batman's escapes aren't quite big enough. For $35. Learn how to use Chromecast with Google Play Movies and more at google.com/chromecast.",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            subtitle = "By Google",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg",
            title = "For Bigger Escape",
            width = 768,
            height = 432
        ),
        Video(
            description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV. For $35.  Find out more at google.com/chromecast.",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            subtitle = "By Google",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg",
            title = "For Bigger Fun",
            width = 768,
            height = 432
        ),
        Video(
            description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV—for the times that call for bigger joyrides. For $35. Learn how to use Chromecast with YouTube and more at google.com/chromecast.",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            subtitle = "By Google",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg",
            title = "For Bigger Joyrides",
            width = 768,
            height = 432
        ),
        Video(
            description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV—for when you want to make Buster's big meltdowns even bigger. For $35. Learn how to use Chromecast with Netflix and more at google.com/chromecast.",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            subtitle = "By Google",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg",
            title = "For Bigger Meltdowns",
            width = 768,
            height = 432
        ),
        Video(
            description = "Sintel is an independently produced short film, initiated by the Blender Foundation as a means to further improve and validate the free/open source 3D creation suite Blender. With initial funding provided by 1000s of donations via the internet community, it has again proven to be a viable development model for both open 3D technology as for independent animation film.\nThis 15 minute film has been realized in the studio of the Amsterdam Blender Institute, by an international team of artists and developers. In addition to that, several crucial technical and creative targets have been realized online, by developers and artists and teams all over the world.\nwww.sintel.org",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            subtitle = "By Blender Foundation",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg",
            title = "Sintel",
            width = 768,
            height = 327
        ),
        Video(
            description = "Smoking Tire takes the all-new Subaru Outback to the highest point we can find in hopes our customer-appreciation Balloon Launch will get some free T-shirts into the hands of our viewers.",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
            subtitle = "By Garage419",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDirt.jpg",
            title = "Subaru Outback On Street And Dirt",
            width = 480,
            height = 270
        ),
        Video(
            description = "Tears of Steel was realized with crowd-funding by users of the open source 3D creation tool Blender. Target was to improve and test a complete open and free pipeline for visual effects in film - and to make a compelling sci-fi film in Amsterdam, the Netherlands.  The film itself, and all raw material used for making it, have been released under the Creatieve Commons 3.0 Attribution license. Visit the tearsofsteel.org website to find out more about this, or to purchase the 4-DVD box with a lot of extras.  (CC) Blender Foundation - http://www.tearsofsteel.org",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            subtitle = "By Blender Foundation",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/TearsOfSteel.jpg",
            title = "Tears of Steel",
            width = 768,
            height = 320
        ),
        Video(
            description = "The Smoking Tire heads out to Adams Motorsports Park in Riverside, CA to test the most requested car of 2010, the Volkswagen GTI. Will it beat the Mazdaspeed3's standard-setting lap time? Watch and see...",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4",
            subtitle = "By Garage419",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/VolkswagenGTIReview.jpg",
            title = "Volkswagen GTI Review",
            width = 480,
            height = 270
        ),
        Video(
            description = "The Smoking Tire is going on the 2010 Bullrun Live Rally in a 2011 Shelby GT500, and posting a video from the road every single day! The only place to watch them is by subscribing to The Smoking Tire or watching at BlackMagicShine.com",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            subtitle = "By Garage419",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/WeAreGoingOnBullrun.jpg",
            title = "We Are Going On Bullrun",
            width = 768,
            height = 432
        ),
        Video(
            description = "The Smoking Tire meets up with Chris and Jorge from CarsForAGrand.com to see just how far $1,000 can go when looking for a car.The Smoking Tire meets up with Chris and Jorge from CarsForAGrand.com to see just how far $1,000 can go when looking for a car.",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4",
            subtitle = "By Garage419",
            thumb = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/WhatCarCanYouGetForAGrand.jpg",
            title = "What care can you get for a grand?",
            width = 480,
            height = 270
        )
    )
}

class FeedComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    Composite<FeedComponent.State>, Stateful<FeedComponent.State> by Stateful.Default(State()) {

    private val scope by lazy { coroutineScope(SupervisorJob()) }

    private val repository by lazy { ProtectoraiRepository() }

    private val incidentTypesFilterComposite by lazy {
        IncidentsTypesFilterComposite(
            scope,
            repository
        )
    }

    private val locationsFilterComposite by lazy { LocationsFilterComposite(scope, repository) }

    @Composable
    override fun Content(modifier: Modifier, state: State) {
        Scaffold(
            modifier = modifier,
            floatingActionButton = {
                FloatingActionButton(onClick = { updateState { copy(isFiltersVisible = true) } }) {
                    Icon(
                        imageVector = ProtectoraiIcons.Filter(),
                        contentDescription = null
                    )
                }
            }
        ) {
            VideoList(
                videos,
                modifier = Modifier.padding(it)
            )
        }
        if (state.isFiltersVisible) {
            @OptIn(ExperimentalMaterial3Api::class)
            ModalBottomSheet(onDismissRequest = { updateState { copy(isFiltersVisible = false) } }) {
                Column(Modifier.padding(8.dp)) {
                    Row {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = { Text("Date start") },
                            singleLine = true,
                            value = state.dateStart,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = {
                                if (it.length <= DATE_CHAR_LENGTH) updateState { copy(dateStart = it) }
                            },
                            visualTransformation = VisualTransformation(::dateFilter)
                        )
                        Spacer(Modifier.size(8.dp))
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = { Text("Date end") },
                            singleLine = true,
                            value = state.dateEnd,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = {
                                if (it.length <= DATE_CHAR_LENGTH) updateState { copy(dateEnd = it) }
                            },
                            visualTransformation = VisualTransformation(::dateFilter)
                        )
                    }
                    incidentTypesFilterComposite.Content(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                    locationsFilterComposite.Content(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun VideoList(
        videos: List<Video>,
        modifier: Modifier = Modifier
    ) {
        val lazyListState = rememberLazyListState()
        val focusIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
        val focusIndexOffset by remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }

        val density = LocalDensity.current

        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp)
        ) {
            items(count = Int.MAX_VALUE) { index ->
                val videoIndex = index % videos.size

                VideoItem(
                    video = videos[videoIndex],
                    focusedVideo = index == 0 && focusIndexOffset <= with(density) { 48.dp.toPx() } ||
                            index == focusIndex + 1 && focusIndexOffset > with(density) { 48.dp.toPx() }
                )
            }
        }
    }

    @Composable
    fun VideoItem(
        video: Video,
        focusedVideo: Boolean,
        modifier: Modifier = Modifier
    ) {
        val animateBackground by animateColorAsState(
            targetValue = if (focusedVideo) Color(0xFFef5350) else MaterialTheme.colorScheme.surface
        )

        Card(
            modifier = modifier.padding(horizontal = 16.dp, 6.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = animateBackground
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .aspectRatio(video.width.toFloat() / video.height.toFloat())
                ) {
                    /*Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onSurface.copy(.1f)),
                        painter = rememberImagePainter(url = video.thumb),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )*/

                    Player(
                        modifier = Modifier.fillMaxSize(),
                        video = video,
                        focusedVideo = focusedVideo
                    )

                    /*androidx.compose.animation.AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp),
                        visible = focusedVideo,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Image(
                            modifier = Modifier
                                .size(32.dp),
                            imageVector = ProtectoraiIcons.Play(),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(0xFFef5350))
                        )
                    }*/
                }

                Text(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 2.dp,
                        end = 8.dp,
                        bottom = 0.dp
                    ),
                    text = video.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 2.dp,
                        end = 8.dp,
                        bottom = 6.dp
                    ),
                    text = video.subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalContentColor.current.copy(.6f)
                )
            }
        }
    }

    @Composable
    private fun Player(
        video: Video,
        focusedVideo: Boolean,
        modifier: Modifier = Modifier
    ) {
        val playerHost = remember { MediaPlayerHost(mediaUrl = video.url) }

        if (focusedVideo) {
            playerHost.play()
        } else {
            playerHost.pause()
        }

        VideoPlayerComposable(
            modifier = modifier,
            playerHost = playerHost
        )
    }

    data class State(
        val isFiltersVisible: Boolean = false,
        val dateStart: String = EMPTY_STRING,
        val dateEnd: String = EMPTY_STRING,
    ) : Composite.State
}

fun dateFilter(annotatedText: AnnotatedString): TransformedText {
    val trimmed = if (annotatedText.text.length >= DATE_CHAR_LENGTH) {
        annotatedText.text.substring(0 until DATE_CHAR_LENGTH)
    } else {
        annotatedText.text
    }
    var out = EMPTY_STRING
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 2 == 1 && i < 4) out += '/'
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = when {
            offset <= 1 -> offset
            offset <= 3 -> offset + 1
            offset <= DATE_CHAR_LENGTH -> offset + 2
            else -> DATE_CHAR_LENGTH + 2
        }

        override fun transformedToOriginal(offset: Int): Int = when {
            offset <= 2 -> offset
            offset <= 5 -> offset - 1
            offset <= DATE_CHAR_LENGTH + 2 -> offset - 2
            else -> DATE_CHAR_LENGTH
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}
