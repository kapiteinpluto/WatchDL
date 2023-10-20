package com.example.watchdl

import android.content.Context
import android.location.Location
import com.example.watchdl.activities.MainActivity
import com.example.watchdl.helperClasses.FileEditor
import com.example.watchdl.objects.BusLine
import com.example.watchdl.objects.BusStop
import org.junit.Assert.*
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.internal.util.io.IOUtil.readLines
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class FileEditorTest {
    private val FAKE_PATH = "/temp"
    private val stop = BusStop(
        100000,
        "Testitem",
        listOf(BusLine("1", "#FF0000", "testDescription"),BusLine("2", "#FFFF00", "testDescription2")),
        Location("")
    )

    @Mock
    private var mockContext = mock<Context> {
        on { filesDir } doReturn File(FAKE_PATH)
    }
    private var fileEditor = FileEditor(mockContext)


    @Test
    fun getFile_test() {
        val result: String = fileEditor.getFile().absolutePath
        Assertions.assertEquals(result, "C:\\temp\\stopSettings")
    }


    @Test
    fun addStop_test() {
        fileEditor.addStop(stop)
        Assertions.assertEquals(fileEditor.getFile().readLines().last(), stop.toString())
    }

    @Test
    fun getStop_test() {
        fileEditor.addStop(stop)
        val requestedStop = fileEditor.getStop(stop.id)
        Assertions.assertEquals(requestedStop.toString(), stop.toString())
    }

    @Test
    fun getStop_stopNotFound_test() {
        val requestedStop = fileEditor.getStop(234)
        Assertions.assertEquals(requestedStop.id, 0)
    }

    @Test
    fun fileToStops_test(){
        val requestedStops = fileEditor.fileToStops()
        Assertions.assertNotEquals(requestedStops.size, 0)
    }

    @Test
    fun updateStop_test(){
        fileEditor.addStop(stop)
        val editedStop = BusStop(stop.toString()).apply { name = "testing" }
        fileEditor.updateStop(editedStop)
        Assertions.assertEquals(fileEditor.getStop(stop.id).name, "testing")

        fileEditor.updateStop(stop)
        Assertions.assertEquals(fileEditor.getStop(stop.id).name, stop.name)
    }

    @Test
    fun deleteStop_test() {
        fileEditor.deleteStop(stop)
        Assertions.assertEquals(fileEditor.getStop(stop.id).name, "Error: stop not found in file")
    }

    @Test
    fun getSettingsFile_test() {
        val result: String = fileEditor.getSettingsFile().absolutePath
        Assertions.assertEquals(result, "C:\\temp\\appSettings")
    }

    @Test
    fun getSetting_test() {
        val settingValue = fileEditor.getSetting("AutoRefresh")
        assertNotNull(settingValue)
    }

    @Test
    fun editSetting_test() {
        fileEditor.editSetting("AutoRefresh", "10")
        Assertions.assertEquals(fileEditor.getSetting("AutoRefresh"), "10")
    }
}