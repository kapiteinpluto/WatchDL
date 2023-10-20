package com.example.watchdl

import android.content.Context
import com.example.watchdl.helperClasses.FileEditor
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class FileEditorTest {
    private val FAKE_PATH = "/temp"


    @Mock
    private var mockContext = mock<Context> {
        on { filesDir } doReturn File(FAKE_PATH)
    }
    private var fileEditor = FileEditor(mockContext)


    @Test
    fun getStops_test() {
        fileEditor.fillFile()
        Assertions.assertEquals(fileEditor.getStops().size, 2)
    }

    @Test
    fun getLocations_test() {
        fileEditor.fillFile()
        Assertions.assertEquals(fileEditor.getLocations().size, 2)
    }

    @Test
    fun fillFile_test() {
        fileEditor.fillFile()
        Assertions.assertEquals(fileEditor.getStops().size, 2)
    }
}