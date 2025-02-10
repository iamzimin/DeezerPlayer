package com.evg.deezerplayer.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme


val bottomNavPadding = 81.dp

@Composable
fun BottomBar(
    navigation: NavController
) {
    val navBackStackEntry by navigation.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarVisible = BottomBarScreen.allScreens.any { bottomBarScreen ->
        currentDestination?.hasRoute(bottomBarScreen.route::class) == true
    }


    AnimatedVisibility(
        visible = bottomBarVisible,
        modifier = Modifier.fillMaxWidth(),
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        Column {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = AppTheme.colors.bottomBarSelected,
            )
            NavigationBar(
                containerColor = AppTheme.colors.background,
            ) {
                BottomBarScreen.allScreens.forEach { screen ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIndicatorColor = AppTheme.colors.bottomBarSelected,
                        ),
                        alwaysShowLabel = false,
                        label = {
                            Text(
                                text = stringResource(screen.title),
                                color = AppTheme.colors.text,
                            )
                        },
                        selected = currentDestination?.hasRoute(screen.route::class) == true,
                        onClick = {
                            navigation.navigate(screen.route) {
                                currentDestination?.route?.let {
                                    popUpTo(it) {
                                        inclusive = true
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = stringResource(screen.title),
                                tint = AppTheme.colors.text,
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun BottomBarPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            BottomBar(
                navigation = NavHostController(LocalContext.current),
            )
        }
    }
}