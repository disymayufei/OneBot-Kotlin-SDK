package cn.disy920.oks.event

import cn.disy920.oks.contact.Friend

interface FriendEvent : Event {
    val friend: Friend
}