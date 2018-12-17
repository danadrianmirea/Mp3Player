package com.example.mike.mp3player.service.library;

import android.content.Context;
import android.net.Uri;

import com.example.mike.mp3player.service.library.utils.MediaLibraryUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({MediaLibraryUtils.class, Uri.class})
public class MediaLibraryTest {

    private static final String MOCK_PATH = "PATH";

    @Mock
    Context context;
    @Mock
    Uri uri;

    MediaLibrary mediaLibrary;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mediaLibrary = new MediaLibrary(context);
    }

    @AfterEach
    public void tearDown () {

    }

    /**
     *
     * @throws IOException
     */

    @Test
    public void rootDirectoryTest() throws IOException {

        File rootDir = new File("rootDir");
        rootDir.mkdir();
        PowerMockito.mockStatic(MediaLibraryUtils.class);
        PowerMockito.mockStatic(Uri.class);
        when(MediaLibraryUtils.getExternalStorageDirectory()).thenReturn(rootDir);
        when(MediaLibraryUtils.getSongTitle(any(), any())).thenCallRealMethod();
        when(Uri.fromFile(any())).thenReturn(uri);
        when(uri.getPath()).thenReturn(MOCK_PATH);

        File mp3_1 = createFile(rootDir, "test1.mp3");
        File mp3_2 = createFile(rootDir, "test2.mp3");
        File noneMp3_1 = createFile(rootDir, "text.txt");
        File childDir = createDirectory(rootDir, "childDir");
        File wav_1 = createFile(childDir, "test4.wav");
        File noneMp3_2 = createFile(childDir, "noExtension");


        mediaLibrary.init();
        noneMp3_2.delete();
        wav_1.delete();
        childDir.delete();
        noneMp3_1.delete();
        mp3_2.delete();
        mp3_1.delete();
        rootDir.delete();

        assertTrue(mediaLibrary.getMediaUri(String.valueOf(MOCK_PATH.hashCode())).equals(uri));
    }

    private File createFile(File parentDir, String name) throws IOException {
        File f = new File(parentDir, name);
        f.createNewFile();
        return f;
    }

    private File createDirectory(File parentDir, String name) throws IOException {
        File f = new File(parentDir, name);
        f.mkdir();
        return f;
    }
}