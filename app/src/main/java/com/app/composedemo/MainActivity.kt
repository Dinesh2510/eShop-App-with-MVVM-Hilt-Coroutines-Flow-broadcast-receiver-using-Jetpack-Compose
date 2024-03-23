package com.app.composedemo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.app.composedemo.include.SimpleLightTopAppBar
import com.app.composedemo.model.Products
import com.app.composedemo.mvvm.ProductViewModel
import com.app.composedemo.utlis.ViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ProductViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    HomePageUI(viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    private fun HomePageUI(viewModel: ProductViewModel) {
        Scaffold(topBar = {
            SimpleLightTopAppBar("eShop")
        }

        ) { paddingValues ->
            val isConnected by viewModel.isConnected.collectAsState()
            val productsState by viewModel.products.collectAsState()
            if (isConnected) {
                // Render UI for when connected
                when (productsState) {
                    is ViewState.Success -> {
                        // Handle success state
                        val productList = (productsState as ViewState.Success).data
                        LazyVerticalGrid(modifier = Modifier
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                            columns = GridCells.Adaptive(150.dp),
                            content = {
                                items(productList.size) { index: Int ->
                                    ProductCards(productList[index])
                                }
                            })
                    }

                    is ViewState.Error -> {
                        // Handle error state
                        val errorMessage = (productsState as ViewState.Error).message
                        // Show an error message to the user
                        ShowToast(errorMessage, paddingValues)
                    }

                    is ViewState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                }

            } else {
                ShowToast("Not Connected to Internet", paddingValues)
            }
        }
    }

    @Composable
    fun ProductCards(
        product: Products,
    ) {
        val ctx = LocalContext.current
        Card(modifier = Modifier
            .height(270.dp)
            .padding(6.dp)
            .clickable {
                val intent = Intent(this, ProductDetailScreen::class.java)
                intent.putExtra("productObject", product);
                startActivity(intent)
            }
            .width(213.dp), colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ), shape = RoundedCornerShape(8.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 15.dp, end = 15.dp, top = 20.dp)
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(100.dp)
                        .width(100.dp), painter = rememberAsyncImagePainter(
                        model = product.image, contentScale = ContentScale.Crop
                    ), contentDescription = "Coffee"
                )
                Text(
                    modifier = Modifier.padding(top = 28.dp),
                    text = product.title,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.Black,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier.padding(top = 3.dp),
                    text = product.description,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Box(modifier = Modifier
                        .size(30.dp)
                        .clickable {}
                        .clip(shape = CircleShape)
                        .background(Color.Black), contentAlignment = Alignment.Center) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.ic_cart),
                            contentDescription = "Icon",
                            tint = Color.White
                        )

                    }
                }
            }
        }
    }

    @Composable
    private fun ShowToast(message: String, paddingValues: PaddingValues) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.connection_error),
                    contentDescription = "contentDescription",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
                Text(message)
            }

        }

    }
}


