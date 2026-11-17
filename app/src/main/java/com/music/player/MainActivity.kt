package com.music.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage

data class Song(val id: Int, val title: String, val artist: String, val url: String, val coverUrl: String)

object MusicRepository {
    val playlist = listOf(
        Song(1, "Big Buck Bunny Audio", "Animation OST", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600"),
        Song(2, "Elephants Dream Ambient", "Blender Foundation", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600"),
        Song(3, "For Bigger Blazes", "Google Samples", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600")
    )
}

class MainActivity : ComponentActivity() {
    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exoPlayer = ExoPlayer.Builder(this).build()

        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121216)) {
                MusicApp(exoPlayer!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicApp(player: ExoPlayer) {
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Моя Музыка", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A22))
            )
        },
        bottomBar = {
            if (currentSong != null) {
                PlayerBar(
                    song = currentSong!!,
                    isPlaying = isPlaying,
                    onPlayPauseClick = {
                        if (player.isPlaying) {
                            player.pause()
                            isPlaying = false
                        } else {
                            player.play()
                            isPlaying = true
                        }
                    }
                )
            }
        },
        containerColor = Color(0xFF121216)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(MusicRepository.playlist) { song ->
                SongItem(song = song, isSelected = currentSong?.id == song.id) {
                    currentSong = song
                    player.setMediaItem(MediaItem.fromUri(song.url))
                    player.prepare()
                    player.play()
                    isPlaying = true
                }
            }
        }
    }
}

@Composable
fun SongItem(song: Song, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF2A2A38) else Color(0xFF1E1E28)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = song.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = song.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = song.artist, color = Color.Gray, fontSize = 13.sp)
            }
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF9333EA))
        }
    }
}

@Composable
fun PlayerBar(song: Song, isPlaying: Boolean, onPlayPauseClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A22)).padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(45.dp).clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = song.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                }
            }
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
