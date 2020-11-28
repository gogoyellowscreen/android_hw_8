package com.example.hw8;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UiAutoTest {
    public static final String SAMPLE_PACKAGE = "com.example.hw8";
    public static final long LAUNCH_TIMEOUT = 5000;
    public static final String TITLE = "title";
    private UiDevice device;

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(getInstrumentation());

        // Start from the home screen
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the blueprint app
        Context context = getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(SAMPLE_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public synchronized void checkAddPostSearch() throws UiObjectNotFoundException {
        clickWith("fab");
        device.findObject(By.res(SAMPLE_PACKAGE, "editText")).setText(TITLE);
        clickWaitWith("button");

        assertThat(device.findObject(By.res(SAMPLE_PACKAGE, TITLE)), is(notNullValue()));
    }

    private void clickWith(String button) {
        device.findObject(By.res(SAMPLE_PACKAGE, button)).click();
    }

    private void clickWaitWith(String button) {
        device.wait(Until.findObject(By.res(SAMPLE_PACKAGE, button)), 1000).click();
    }

    @Test
    public synchronized void disableInternetResetDoNothing() throws IOException {
        clickWith("reset");
        clickWaitWith("fab");
        onView(withId(R.id.myRecyclerView)).check(new RecyclerViewItemCountAssertion(101));

        device.executeShellCommand("svc wifi disable");
        device.executeShellCommand("svc data disable");

        clickWaitWith("reset");
        onView(withId(R.id.myRecyclerView)).check(new RecyclerViewItemCountAssertion(101));


        device.executeShellCommand("svc wifi enable");
        device.executeShellCommand("svc data enable");
    }

    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = getApplicationContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}
