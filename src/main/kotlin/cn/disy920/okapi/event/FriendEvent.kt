package cn.disy920.okapi.event

import cn.disy920.okapi.contact.Friend

interface FriendEvent : Event {
    val friend: Friend
}