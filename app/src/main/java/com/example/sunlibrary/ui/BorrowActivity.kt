package com.example.sunlibrary.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sunlibrary.ui.theme.SunLibraryTheme

@SuppressLint("NotConstructor")
@Composable
fun BorrowActivity() {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // TODO 作成中です。この画面に以下の機能がある
        //  ①ユーザが借りた本を一覧表示
        //  ②借りたい本を検索できるし、その本のステータスも確認できる

        Spacer(modifier = Modifier.weight(1.0f))
        AppName()
        Text("会員画面")
        Spacer(modifier = Modifier.weight(2.0f))
    }
}
@Preview(showBackground = true)
@Composable
fun FreeLiftPreview() {
    SunLibraryTheme {
        BorrowActivity()
    }
}
