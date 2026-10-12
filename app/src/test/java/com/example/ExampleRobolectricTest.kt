package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("3D Building Visualizer", appName)
  }

  @Test
  fun `building plan and json conversion works correctly`() {
    val sample = com.example.data.model.BuildingPlan.createSample("Test Residence")
    assertEquals("Test Residence", sample.name)
    assertEquals(2, sample.floorCount)

    val entity = com.example.data.local.BuildingJsonConverter.toEntity(sample)
    assertEquals("Test Residence", entity.name)

    val convertedBack = com.example.data.local.BuildingJsonConverter.toModel(entity)
    assertEquals(sample.name, convertedBack.name)
    assertEquals(sample.floors.size, convertedBack.floors.size)

    val threeJsJson = com.example.data.local.BuildingJsonConverter.toThreeJsJson(sample)
    org.junit.Assert.assertTrue(threeJsJson.contains("Test Residence"))
    org.junit.Assert.assertTrue(threeJsJson.contains("Ground Floor"))
  }
}

