package com.example.guessthesong.utils

import com.example.guessthesong.R


fun <T> arrayListOf(vararg elements: T): ArrayList<T> {
    val list = ArrayList<T>()
    for (element in elements) {
        list.add(element)
    }
    return list
}

fun getImage(albumImage: String): Int {
    val imageNameList = arrayListOf<String>("Like a Rolling Stone","Life on Mars","Your Song","Sweet Child o' Mine","Imagine", "Over the Rainbow","Stairway to Heaven","Billie Jean", "Smells Like Teen Spirit","Live Forever", "Bohemian Rhapsody","(I Can't Get No) Satisfaction","God Save the Queen", "Hey Jude","London Calling", "Hotel California", "Waterloo Sunset", "One", "I Will Always Love You","Ladbroke Grove","Taste (Make It Shake)","Don't Call Me Angel","Professor X","Outnumbered", "3 Nights","Take Me Back to London","Both","Sorry","Be Honest","Higher Love","Ransom","Circles","Goodbyes","Ride It","Post Malone","How Do You Sleep?","Senorita","Dance Monkey","Strike A Pose")
    val imageList = intArrayOf(R.drawable.like_a_rolling_stone,R.drawable.life_on_mars,R.drawable.your_song,R.drawable.sweet_child,R.drawable.imagine,R.drawable.over_the_rainbow,R.drawable.stairway_to_heaven,R.drawable.billie_jean,R.drawable.smells_like_teen_spirit,R.drawable.live_forever,R.drawable.bohemian_rhapsody,R.drawable.satisfaction,R.drawable.god_save_the_queen,R.drawable.hey_jude,R.drawable.london_calling,R.drawable.hotel_california,R.drawable.waterloo_sunset,R.drawable.one,R.drawable.will_always_love,R.drawable.ladbrook,R.drawable.taste,R.drawable.dont_call_me_angel,R.drawable.top_boy,R.drawable.outnumbered,R.drawable.nights,R.drawable.take_me_back,R.drawable.both,R.drawable.sorry,R.drawable.be_honest,R.drawable.higher_love,R.drawable.ransom,R.drawable.circles,R.drawable.goodbyes,R.drawable.ride_it,R.drawable.post_malone,R.drawable.how_do_you_sleep,R.drawable.senorita,R.drawable.dance_monkey,R.drawable.strike_a_pose)
    if (imageNameList.contains(albumImage)){
        var imageIndex = imageNameList.indexOf(albumImage)
        return imageList.get(imageIndex)
    }
    return R.drawable.ic_close
}