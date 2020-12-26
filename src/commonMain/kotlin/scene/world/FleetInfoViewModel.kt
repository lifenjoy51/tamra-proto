package scene.world

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.Bitmap32
import ui.LiveData

class FleetInfoViewModel internal constructor(
        val shipImage: LiveData<Bitmap> = LiveData(Bitmap32(0, 0)),
        val shipName: LiveData<String> = LiveData(""),
        val shipSpeed: LiveData<String> = LiveData(""),
        val shipTypeName: LiveData<String> = LiveData(""),
        val shipCargos: LiveData<String> = LiveData("")
) {
    companion object {
        val instance: FleetInfoViewModel = FleetInfoViewModel()
    }
}