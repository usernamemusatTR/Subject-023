package com.denek023.event;

import java.io.IOException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import javax.swing.*;

public class CameraOpener {
    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class);
        int MessageBoxW(int hWnd, String lpText, String lpCaption, int uType);
    }

    public static void openCamera() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
                Runtime.getRuntime().exec(
                    "powershell -command \"(Add-Type '[DllImport(\\\"user32.dll\\\")]^public static extern bool ShowWindowAsync(IntPtr hWnd, int nCmdShow);' -Name Win32 -Namespace NativeMethods -PassThru)::ShowWindowAsync((Get-Process javaw).MainWindowHandle, 6)\"");
            }

            Runtime.getRuntime().exec("cmd /c start microsoft.windows.camera:");

            java.io.File tempVbs = java.io.File.createTempFile("CameraMsgBox", ".vbs");
            String vbsContent = "MsgBox \"Say Cheese :)\", 0, \"Smile\"";
            java.io.FileWriter fw = new java.io.FileWriter(tempVbs);
            try {
                fw.write(vbsContent);
                fw.close();
            } catch (Throwable throwable) {
                try { fw.close(); } catch (Throwable t2) { throwable.addSuppressed(t2); }
                throw throwable;
            }
            Runtime.getRuntime().exec("wscript \"" + tempVbs.getAbsolutePath() + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}