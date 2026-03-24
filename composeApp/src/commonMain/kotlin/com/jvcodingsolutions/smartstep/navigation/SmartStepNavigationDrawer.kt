package com.jvcodingsolutions.smartstep.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.exit
import smartstep.composeapp.generated.resources.fix_the_stop_counting_issue
import smartstep.composeapp.generated.resources.personal_settings
import smartstep.composeapp.generated.resources.step_goal

@Composable
fun SmartStepNavigationDrawer(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    drawerState: DrawerState,
    hasBackgroundPermission: Boolean,
    onFixStopCountingIssueClick: () -> Unit,
    onShowExitDialog: () -> Unit,
    onStepGoalClick: () -> Unit,
    onPersonalSettingsClick: () -> Unit,
    content: @Composable () -> Unit
) {

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.backgroundSecondary
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                DrawerLayout(
                    modifier = Modifier,
                    selectedKey = selectedKey,
                    hasBackgroundPermission = hasBackgroundPermission,
                    onFixStopCountingIssueClick = onFixStopCountingIssueClick,
                    onSelectKey = onSelectKey,
                    onShowExitDialog = onShowExitDialog,
                    onStepGoalClick = onStepGoalClick,
                    onPersonalSettingsClick = onPersonalSettingsClick
                )
            }
        },
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        gesturesEnabled = true,
        content = content,
    )
}

@Composable
private fun DrawerLayout(
    modifier: Modifier = Modifier,
    selectedKey: NavKey,
    hasBackgroundPermission: Boolean,
    onFixStopCountingIssueClick: () -> Unit,
    onSelectKey: (NavKey) -> Unit,
    onShowExitDialog: () -> Unit,
    onStepGoalClick: () -> Unit,
    onPersonalSettingsClick: () -> Unit
) {

    if (!hasBackgroundPermission) {
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(Res.string.fix_the_stop_counting_issue),
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = MaterialTheme.colorScheme.buttonPrimary
                )
            },
            selected = false,
            onClick = {
                onFixStopCountingIssueClick()
            },
            modifier = Modifier
                .padding(12.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.backgroundSecondary,
                unselectedContainerColor = MaterialTheme.colorScheme.backgroundSecondary,
                selectedTextColor = MaterialTheme.colorScheme.buttonPrimary
            ),
            shape = RectangleShape
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.strokeMain
        )
    }

    NavigationDrawerItem(
        label = {
            Text(
                text = stringResource(Res.string.step_goal),
                style = MaterialTheme.typography.bodyLargeMedium,
                color = MaterialTheme.colorScheme.textPrimary
            )
        },
        selected = false,
        onClick = {
            onStepGoalClick()
        },
        modifier = Modifier
            .padding(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.backgroundSecondary,
            unselectedContainerColor = MaterialTheme.colorScheme.backgroundSecondary,
            selectedTextColor = MaterialTheme.colorScheme.textPrimary
        ),
        shape = RectangleShape
    )

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.strokeMain
    )

    NavigationDrawerItem(
        label = {
            Text(
                text = stringResource(Res.string.personal_settings),
                style = MaterialTheme.typography.bodyLargeMedium,
                color = MaterialTheme.colorScheme.textPrimary
            )
        },
        selected = false,
        onClick = {
            onPersonalSettingsClick()
        },
        modifier = Modifier
            .padding(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.backgroundSecondary,
            unselectedContainerColor = MaterialTheme.colorScheme.backgroundSecondary,
            selectedTextColor = MaterialTheme.colorScheme.textPrimary
        ),
        shape = RectangleShape
    )

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.strokeMain
    )


    NavigationDrawerItem(
        label = {
            Text(
                text = stringResource(Res.string.exit),
                style = MaterialTheme.typography.bodyLargeMedium,
                color = MaterialTheme.colorScheme.buttonPrimary
            )
        },
        selected = false,
        onClick = {
            onShowExitDialog()
        },
        modifier = Modifier
            .padding(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.backgroundSecondary,
            unselectedContainerColor = MaterialTheme.colorScheme.backgroundSecondary,
            selectedTextColor = MaterialTheme.colorScheme.textPrimary
        ),
        shape = RectangleShape
    )
}
