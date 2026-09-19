package com.kairon.testingapk.overlay
data class OverlayConfig(
    var players:Boolean=true,var bots:Boolean=true,var skeleton:Boolean=true,
    var distance:Boolean=true,var direction:Boolean=true,var weapons:Boolean=true,
    var items:Boolean=true,var vehicles:Boolean=true,var occupants:Boolean=true,
    var counter:Boolean=true,var diagnostics:Boolean=true,var debug:Boolean=false,
    var rangeMeters:Float=500f
)
