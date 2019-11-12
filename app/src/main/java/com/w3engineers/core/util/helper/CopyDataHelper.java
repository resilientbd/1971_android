package com.w3engineers.core.util.helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class CopyDataHelper {
  public static void copyText(Context context,String copiedText)
  {
      ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
      ClipData clip = ClipData.newPlainText(copiedText, copiedText);
      clipboard.setPrimaryClip(clip);
  }
}
