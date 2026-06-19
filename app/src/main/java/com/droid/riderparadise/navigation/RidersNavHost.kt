package com.droid.riderparadise.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.droid.riderparadise.feature.auth.AuthScreen
import com.droid.riderparadise.feature.feedback.FeedbackScreen
import com.droid.riderparadise.feature.onboarding.JoinGroupScreen
import com.droid.riderparadise.feature.onboarding.ProfileScreen
import com.droid.riderparadise.feature.ride.ChatScreen
import com.droid.riderparadise.feature.ride.LiveRideScreen
import com.droid.riderparadise.feature.ride.LobbyScreen
import com.droid.riderparadise.feature.ride.PlanRideScreen
import com.droid.riderparadise.feature.ride.RecapScreen
import com.droid.riderparadise.feature.riders.SuggestionsScreen

@Composable
fun RidersNavHost(
    startDestination: String,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.AUTH) {
            AuthScreen(
                onAuthenticatedNew = {
                    navController.navigate(Routes.ONBOARDING_PROFILE) { popUpTo(Routes.AUTH) { inclusive = true } }
                },
                onAuthenticatedExisting = {
                    navController.navigate(Routes.MAIN) { popUpTo(Routes.AUTH) { inclusive = true } }
                },
            )
        }

        composable(Routes.ONBOARDING_PROFILE) {
            ProfileScreen(onContinue = { navController.navigate(Routes.ONBOARDING_JOIN) })
        }

        composable(Routes.ONBOARDING_JOIN) {
            JoinGroupScreen(
                onEnter = {
                    navController.navigate(Routes.MAIN) { popUpTo(Routes.ONBOARDING_PROFILE) { inclusive = true } }
                },
            )
        }

        composable(Routes.MAIN) {
            MainScaffold(
                onPlanRide = { gid, gname -> navController.navigate(Routes.plan(gid, gname)) },
                onOpenLobby = { rid -> navController.navigate(Routes.lobby(rid)) },
                onOpenLive = { rid -> navController.navigate(Routes.live(rid)) },
                onOpenRecap = { rid -> navController.navigate(Routes.recap(rid)) },
                onOpenSuggestions = { navController.navigate(Routes.SUGGESTIONS) },
                onOpenFeedback = { navController.navigate(Routes.FEEDBACK) },
                onSignedOut = {
                    navController.navigate(Routes.AUTH) { popUpTo(Routes.MAIN) { inclusive = true } }
                },
            )
        }

        composable(Routes.SUGGESTIONS) {
            SuggestionsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.FEEDBACK) {
            FeedbackScreen(onBack = { navController.popBackStack() })
        }

        composable(
            Routes.PLAN,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType },
                navArgument("groupName") { type = NavType.StringType },
            ),
        ) { entry ->
            PlanRideScreen(
                groupId = entry.arguments?.getString("groupId").orEmpty(),
                groupName = entry.arguments?.getString("groupName").orEmpty(),
                onBack = { navController.popBackStack() },
                onPublished = { rid ->
                    navController.navigate(Routes.lobby(rid)) { popUpTo(Routes.MAIN) }
                },
            )
        }

        composable(Routes.LOBBY, arguments = listOf(navArgument("rideId") { type = NavType.StringType })) { entry ->
            LobbyScreen(
                rideId = entry.arguments?.getString("rideId").orEmpty(),
                onBack = { navController.popBackStack() },
                onStart = { rid -> navController.navigate(Routes.live(rid)) { popUpTo(Routes.MAIN) } },
            )
        }

        composable(Routes.LIVE, arguments = listOf(navArgument("rideId") { type = NavType.StringType })) { entry ->
            val rid = entry.arguments?.getString("rideId").orEmpty()
            LiveRideScreen(
                rideId = rid,
                onBack = { navController.popBackStack() },
                onOpenChat = { navController.navigate(Routes.chat(rid)) },
                onEndRide = { navController.navigate(Routes.recap(rid)) { popUpTo(Routes.MAIN) } },
            )
        }

        composable(Routes.CHAT, arguments = listOf(navArgument("rideId") { type = NavType.StringType })) { entry ->
            ChatScreen(
                rideId = entry.arguments?.getString("rideId").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.RECAP, arguments = listOf(navArgument("rideId") { type = NavType.StringType })) { entry ->
            RecapScreen(
                rideId = entry.arguments?.getString("rideId").orEmpty(),
                onBack = { navController.navigate(Routes.MAIN) { popUpTo(Routes.MAIN) { inclusive = true } } },
            )
        }
    }
}
