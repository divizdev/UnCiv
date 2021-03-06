package com.unciv.ui.pickerscreens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.unciv.logic.map.TileInfo
import com.unciv.models.gamebasics.GameBasics
import com.unciv.models.gamebasics.tile.TileImprovement
import com.unciv.models.gamebasics.tr
import com.unciv.ui.utils.*

class ImprovementPickerScreen(tileInfo: TileInfo, onAccept: ()->Unit) : PickerScreen() {
    private var selectedImprovement: TileImprovement? = null

    init {
        val currentPlayerCiv = game.gameInfo.getCurrentPlayerCivilization()
        setDefaultCloseAction()

        fun accept(improvement: TileImprovement?) {
            if (improvement != null) {
                tileInfo.startWorkingOnImprovement(improvement, currentPlayerCiv)
                if (tileInfo.civilianUnit != null) tileInfo.civilianUnit!!.action = null // this is to "wake up" the worker if it's sleeping
                onAccept()
                game.setWorldScreen()
                dispose()
            }
        }

        rightSideButton.setText("Pick improvement".tr())
        rightSideButton.onClick {
            accept(selectedImprovement)
        }

        val regularImprovements = VerticalGroup()
        regularImprovements.space(10f)

        for (improvement in GameBasics.TileImprovements.values) {
            if (!tileInfo.canBuildImprovement(improvement, currentPlayerCiv)) continue
            if(improvement.name == tileInfo.improvement) continue
            if(improvement.name==tileInfo.improvementInProgress) continue

            val group = Table()

            val image = if(improvement.name.startsWith("Remove"))
                ImageGetter.getImage("OtherIcons/Stop")
            else
                ImageGetter.getImprovementIcon(improvement.name,30f)

            group.add(image).size(30f).pad(10f)

            group.add(Label(improvement.name.tr() + " - " + improvement.getTurnsToBuild(currentPlayerCiv) + " {turns}".tr(),skin)
                    .setFontColor(Color.WHITE)).pad(10f)

            group.touchable = Touchable.enabled
            group.onClick {
                selectedImprovement = improvement
                pick(improvement.name.tr())
                descriptionLabel.setText(improvement.description)
            }

            val pickNow = "Pick now!".toLabel()
            pickNow.onClick {
                accept(improvement)
            }

            val improvementButton = Button(skin)
            improvementButton.add(group).padRight(10f).fillY()
            improvementButton.addSeparatorVertical()
            improvementButton.add(pickNow).padLeft(10f).fill()
            regularImprovements.addActor(improvementButton)

        }
        topTable.add(regularImprovements)
    }
}

