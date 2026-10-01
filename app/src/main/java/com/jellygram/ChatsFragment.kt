package com.jellygram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

// Заглушка под список сообщений.
// В реальном моде exteraGram сюда встраивается DialogsActivity.
class ChatsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }
}
