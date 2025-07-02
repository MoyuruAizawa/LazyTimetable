package io.moyuru.lazytimetablesample

sealed interface Period {
  val durationSec: Int
  val columnNum: Int

  data class Show(
    val title: String,
    override val durationSec: Int,
    override val columnNum: Int,
  ) : Period

  data class Empty(
    override val durationSec: Int,
    override val columnNum: Int,
  ) : Period
}

data class Stage(
  val title: String,
  val periods: List<Period>,
)

val stages = listOf(
  Stage(
    title = "MAINSTAGE",
    periods = listOf(
      Period.Show("Daybreak: ANNA", 9000, 0), // 12:00 - 14:30 (2.5 hours)
      Period.Show("Mike Williams", 3600, 0), // 14:30 - 15:30 (1 hour)
      Period.Show("Odymel b2b Pegassi", 3600, 0), // 15:30 - 16:30 (1 hour)
      Period.Show("NERVO", 3600, 0), // 16:30 - 17:30 (1 hour)
      Period.Show("Vini Vici", 3600, 0), // 17:35 - 18:35 (1 hour)
      Period.Show("Sub Zero Project", 3600, 0), // 18:35 - 19:35 (1 hour)
      Period.Show("Meduza", 3600, 0), // 19:40 - 20:40 (1 hour)
      Period.Show("Artbat b2b Kölsch", 3600, 0), // 20:40 - 21:40 (1 hour)
      Period.Show("Alok", 3900, 0), // 21:45 - 22:45 (1 hour)
      Period.Show("Axwell", 3900, 0), // 22:50 - 23:50 (1 hour)
      Period.Show("Martin Garrix", 4200, 0), // 23:50 - 00:50 (1 hour)
    )
  ),

  Stage(
    title = "FREEDOM BY BUD",
    periods = listOf(
      Period.Show("Esin Boz", 7200, 1), // 12:00 - 14:00 (2 hours)
      Period.Show("Semsei", 5400, 1), // 14:00 - 15:30 (1.5 hours)
      Period.Show("Rivo", 5400, 1), // 15:30 - 17:00 (1.5 hours)
      Period.Show("Layla Benitez", 4800, 1), // 17:00 - 18:20 (1.33 hours)
      Period.Show("Joris Voorn b2b Rose Ringed", 6000, 1), // 18:30 - 20:00 (1.5 hours)
      Period.Show("HI-LO B2B Eli Brown", 5400, 1), // 20:00 - 21:30 (1.5 hours)
      Period.Show("John Summit", 5400, 1), // 21:30 - 23:00 (1.5 hours)
      Period.Show("Eric Prydz", 1800, 1), // 23:00 - 00:30 (1.5 hours)
    )
  ),

  Stage(
    title = "THE ROSE GARDEN",
    periods = listOf(
      Period.Show("Sennix", 3600, 2), // 12:00 - 13:00 (1 hour)
      Period.Show("Idle Days", 3600, 2), // 13:00 - 14:00 (1 hour)
      Period.Show("IVY", 3600, 2), // 14:00 - 15:00 (1 hour)
      Period.Show("Pirapus", 3600, 2), // 15:00 - 16:00 (1 hour)
      Period.Show("T & Sugah", 3600, 2), // 16:00 - 17:00 (1 hour)
      Period.Show("Primate", 3600, 2), // 17:00 - 18:00 (1 hour)
      Period.Show("1991", 4500, 2), // 18:00 - 19:15 (1.25 hours)
      Period.Show("Emily Makis & Deadline", 2700, 2), // 19:15 - 20:00 (0.75 hours)
      Period.Show("Metrik", 3600, 2), // 20:00 - 21:00 (1 hour)
      Period.Show("Andromedik", 3600, 2), // 21:00 - 22:00 (1 hour)
      Period.Show("Sota b2b Basstripper", 3600, 2), // 22:00 - 23:00 (1 hour)
      Period.Show("Camo & Krooked", 3600, 2), // 23:00 - 00:00 (1 hour)
    )
  ),

  Stage(
    title = "ELIXIR",
    periods = listOf(
      Period.Empty(3600, 3), // 12:00 - 13:00 (1 hour)
      Period.Show("Cromanty Sound", 5400, 3), // 13:00 - 14:30 (1.5 hours)
      Period.Show("Belgianly Made", 3600, 3), // 14:30 - 15:30 (1 hour)
      Period.Show("Hunter", 3600, 3), // 15:30 - 16:30 (1 hour)
      Period.Show("Soul Shakers", 3600, 3), // 16:30 - 17:30 (1 hour)
      Period.Show("Walshy Fire", 3600, 3), // 17:30 - 18:30 (1 hour)
      Period.Show("Flavour Drop", 5400, 3), // 18:30 - 20:00 (1.5 hours)
      Period.Show("Annabel Stop It", 3600, 3), // 20:00 - 21:00 (1 hour)
      Period.Show("Flo Windey ft.Skyve", 3600, 3), // 21:00 - 22:00 (1 hour)
      Period.Show("Double D", 5400, 3), // 22:00 - 23:30 (1.5 hours)
      Period.Show("C-Track", 1800, 3), // 23:30 - 01:00 (1.5 hours)
    )
  ),

  Stage(
    title = "CAGE",
    periods = listOf(
      Period.Show("Sakyra", 3600, 4), // 12:00 - 13:00 (1 hour)
      Period.Show("Pinotello", 3600, 4), // 13:00 - 14:00 (1 hour)
      Period.Show("Spitnoise", 3600, 4), // 14:00 - 15:00 (1 hour)
      Period.Show("DRS b2b Sandy Warez", 3600, 4), // 15:00 - 16:00 (1 hour)
      Period.Show("Toxic Twins b2b NRKi", 3600, 4), // 16:00 - 17:00 (1 hour)
      Period.Show("Sacha Malice", 3600, 4), // 17:00 - 18:00 (1 hour)
      Period.Show("Suttlek b2b Revenja", 3600, 4), // 18:00 - 19:00 (1 hour)
      Period.Show("Revedge b2b Bazzy", 3600, 4), // 19:00 - 20:00 (1 hour)
      Period.Show("Dr Donk", 3600, 4), // 20:00 - 21:00 (1 hour)
      Period.Show("The Dark Horror", 3600, 4), // 21:00 - 22:00 (1 hour)
      Period.Show("Unicorn On K", 3600, 4), // 22:00 - 23:00 (1 hour)
    )
  ),

  Stage(
    title = "THE RAVE CAVE",
    periods = listOf(
      Period.Empty(3600, 5), // 12:00 - 13:00 (1 hour)
      Period.Show("Kriss Reeve", 7200, 5), // 13:00 - 15:00 (2 hours)
      Period.Show("Horizontal Soundsystem", 5400, 5), // 15:00 - 16:30 (1.5 hours)
      Period.Show("Lou8", 5400, 5), // 16:30 - 18:00 (1.5 hours)
      Period.Show("NOME.", 1800, 5), // 18:00 - 18:30 (0.5 hours)
      Period.Show("Ben Malone", 9000, 5), // 18:30 - 21:00 (2.5 hours)
      Period.Empty(3600, 5), // 21:00 - 22:00 (1 hour)
      Period.Show("The Rocketman", 3600, 5), // 22:00 - 23:00 (1 hour)
      Period.Show("Luca v/d Hombergh", 3600, 5), // 23:00 - 00:00 (1 hour)
    )
  ),

  Stage(
    title = "PLANAXIS",
    periods = listOf(
      Period.Show("Arten", 5400, 6), // 12:00 - 13:30 (1.5 hours)
      Period.Show("Laura Hoogland", 5400, 6), // 13:30 - 15:00 (1.5 hours)
      Period.Show("ELIF", 5400, 6), // 15:00 - 16:30 (1.5 hours)
      Period.Show("Anji b2b Axel Haube", 5400, 6), // 16:30 - 18:00 (1.5 hours)
      Period.Show("Brina Knauss", 5400, 6), // 18:00 - 19:30 (1.5 hours)
      Period.Show("Innellea", 5400, 6), // 19:30 - 21:00 (1.5 hours)
      Period.Show("Stephan Bodzin (live)", 7200, 6), // 21:00 - 23:00 (2 hours)
    )
  ),

  Stage(
    title = "RISE BY COCA-COLA",
    periods = listOf(
      Period.Show("More to be announced", 5400, 7), // 12:00 - 13:30 (1.5 hours)
      Period.Show("Vinnie", 3600, 7), // 13:00 - 14:00 (1 hour)
      Period.Show("ZT Music", 3600, 7), // 14:00 - 15:00 (1 hour)
      Period.Show("Bastin", 3600, 7), // 15:00 - 16:00 (1 hour)
      Period.Show("Romy Janssen", 3600, 7), // 16:00 - 17:00 (1 hour)
      Period.Show("Rafi Khan", 3600, 7), // 17:00 - 18:00 (1 hour)
      Period.Show("Roma", 5400, 7), // 18:00 - 19:30 (1.5 hours)
      Period.Show("Disco Dasco", 5400, 7), // 19:30 - 21:00 (1.5 hours)
      Period.Show("Christophe & Seelen", 3600, 7), // 21:00 - 22:00 (1 hour)
      Period.Show("Yves Deruyter", 3600, 7), // 22:00 - 23:00 (1 hour)
    )
  ),

  Stage(
    title = "ATMOSPHERE",
    periods = listOf(
      Period.Show("Hurts", 5400, 8), // 12:00 - 13:30 (1.5 hours)
      Period.Show("Emilija", 5400, 8), // 13:30 - 15:00 (1.5 hours)
      Period.Show("Faster horses", 5400, 8), // 15:00 - 16:30 (1.5 hours)
      Period.Show("Hannah Laing", 5400, 8), // 16:30 - 18:00 (1.5 hours)
      Period.Show("VIDO b2b BYØRN", 5400, 8), // 18:00 - 19:30 (1.5 hours)
      Period.Show("NOVAH", 5400, 8), // 19:30 - 21:00 (1.5 hours)
      Period.Show("Azyir b2b Nico Moreno", 7200, 8), // 21:00 - 23:00 (2 hours)
      Period.Show("Holy Priest", 3600, 8), // 23:00 - 00:00 (1 hour)
      Period.Show("Fantasm", 1980, 8), // 00:00 - 00:55 (0.92 hours)
    )
  ),

  Stage(
    title = "CORE",
    periods = listOf(
      Period.Empty(1800, 9), // 12:00 - 12:30 (0.5 hours)
      Period.Show("Toolate Groove", 7200, 9), // 12:30 - 14:30 (2 hours)
      Period.Show("Laura Meester", 5400, 9), // 14:30 - 16:00 (1.5 hours)
      Period.Show("Parallelle", 5400, 9), // 16:00 - 17:30 (1.5 hours)
      Period.Show("Tripolism", 5400, 9), // 17:30 - 19:00 (1.5 hours)
      Period.Show("Jennifer Cardini", 7200, 9), // 19:00 - 21:00 (2 hours)
      Period.Show("Bora Uzer", 7200, 9), // 21:00 - 23:00 (2 hours)
      Period.Show("Damian Lazarus", 3000, 9), // 23:00 - 00:50 (1.83 hours)
    )
  ),

  Stage(
    title = "CRYSTAL GARDEN",
    periods = listOf(
      Period.Show("Eridu", 7200, 10), // 12:00 - 14:00 (2 hours)
      Period.Show("Franc Fala", 5400, 10), // 14:00 - 15:30 (1.5 hours)
      Period.Show("Austin Millz", 5400, 10), // 15:30 - 17:00 (1.5 hours)
      Period.Show("Arodes b2b Shimza", 5400, 10), // 17:00 - 18:30 (1.5 hours)
      Period.Show("ANNA b2b Antdot", 7200, 10), // 18:30 - 20:30 (2 hours)
      Period.Show("Vintage Culture", 10800, 10), // 20:30 - 23:30 (3 hours)
    )
  ),

  Stage(
    title = "THE GREAT LIBRARY",
    periods = listOf(
      Period.Show("MagiK", 3600, 11), // 12:00 - 13:00 (1 hour)
      Period.Show("Manuals", 3600, 11), // 13:00 - 14:00 (1 hour)
      Period.Show("Juicy M", 3600, 11), // 14:00 - 15:00 (1 hour)
      Period.Show("Pat Krimson", 3600, 11), // 15:00 - 16:00 (1 hour)
      Period.Show("Matisse & Sadko b2b Third Party", 3600, 11), // 16:00 - 17:00 (1 hour)
      Period.Show("Jaden Bojsen", 3600, 11), // 17:00 - 18:00 (1 hour)
      Period.Show("Oliver Heldens", 3600, 11), // 18:00 - 19:00 (1 hour)
      Period.Show("Dillon Francis...", 3600, 11), // 19:00 - 20:00 (1 hour)
      Period.Show("John Newman", 3600, 11), // 20:00 - 21:00 (1 hour)
      Period.Show("MANDY", 3600, 11), // 21:00 - 22:00 (1 hour)
      Period.Show("R3hab", 3600, 11), // 22:00 - 23:00 (1 hour)
      Period.Show("Hardwell", 3600, 11), // 23:00 - 00:00 (1 hour)
    )
  ),

  Stage(
    title = "MELODIA BY CORONA",
    periods = listOf(
      Period.Show("B'Elle", 5400, 12), // 12:00 - 13:30 (1.5 hours)
      Period.Show("Lotte Feyen", 5400, 12), // 13:30 - 15:00 (1.5 hours)
      Period.Show("Arado", 5400, 12), // 15:00 - 16:30 (1.5 hours)
      Period.Show("James de Torres", 5400, 12), // 16:30 - 18:00 (1.5 hours)
      Period.Show("Alure", 5400, 12), // 18:00 - 19:30 (1.5 hours)
      Period.Show("Joezi", 5400, 12), // 19:30 - 21:00 (1.5 hours)
      Period.Show("Aaron Sevilla", 5400, 12), // 21:00 - 22:30 (1.5 hours)
      Period.Show("Block & Crown", 5400, 12), // 22:30 - 00:00 (1.5 hours)
    )
  ),

  Stage(
    title = "HOUSE OF FORTUNE BY JBL",
    periods = listOf(
      Period.Empty(3600, 13), // 12:00 - 13:00 (1 hour)
      Period.Show("Makasi", 3600, 13), // 13:00 - 14:00 (1 hour)
      Period.Show("Little D", 3600, 13), // 14:00 - 15:00 (1 hour)
      Period.Show("More to be announced", 3600, 13), // 15:00 - 16:00 (1 hour)
      Period.Show("Broz", 3600, 13), // 16:00 - 17:00 (1 hour)
      Period.Show("J8man", 3600, 13), // 17:00 - 18:00 (1 hour)
      Period.Show("Cence Brothers", 3600, 13), // 18:00 - 19:00 (1 hour)
      Period.Show("Ely Oaks", 3600, 13), // 19:00 - 20:00 (1 hour)
      Period.Show("Jaden Bojsen", 3600, 13), // 20:00 - 21:00 (1 hour)
      Period.Show("Junkie Kid", 3600, 13), // 21:00 - 22:00 (1 hour)
    )
  ),

  Stage(
    title = "MOOSEBAR",
    periods = listOf(
      Period.Show("DJ Prosit", 14400, 14), // 12:00 - 16:00 (4 hours)
      Period.Show("Tom Cosyns", 10800, 14), // 16:00 - 19:00 (3 hours)
      Period.Show("Kurt Verheyen", 18000, 14), // 19:20 - 00:00 (4.67 hours)
    )
  )
)
