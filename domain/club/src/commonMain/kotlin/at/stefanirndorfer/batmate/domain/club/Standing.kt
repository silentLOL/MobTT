package at.stefanirndorfer.batmate.domain.club

data class Standing(
    val position: Int,
    val teamId: String,
    val teamName: String,
    val played: Int,
    val points: Int,
)
