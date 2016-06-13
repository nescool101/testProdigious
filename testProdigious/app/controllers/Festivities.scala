package controllers

import models._
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.JsonUtils


class Festivities extends Controller with JsonUtils{

  def getFestivities = Action { implicit request =>
    val Festivities = Festivitie.findAll()

    val FestivitiesJson = Json.obj("festivities" ->
      Festivities.map{ te =>
        val festivitieData = FestivitieData.fromFestivitie(te)
        addSelfLink(Json.toJson(festivitieData), routes.Festivities.getFestivitie(te.id))
      }
    )

    Ok(FestivitiesJson)
  }

  def getFestivitie(id: Int) = Action { implicit request =>
    Festivitie.find(id) match{
      case Some(festivitie) =>
        val festivitieData = FestivitieData.fromFestivitie(festivitie)

        val festivitieJson = addSelfLink(Json.toJson(festivitieData), routes.Festivities.getFestivitie(id))

      case None => NotFound(errorJson("El recurso buscado no pudo ser encontrado"))
    }
  }

  def createFestivitie = Action(parse.json) { implicit request =>
    request.body.validate[FestivitieData].fold(
      errors => BadRequest(errorJson(errors)),
      festivitieData =>
        if(!festivitieData.checkTime)
          BadRequest(errorJson("Fecha inicial debe estar antes de fecha final"))
        else{
          val festivitie = festivitieData.create
          Created.withHeaders(LOCATION -> routes.Festivities.getFestivitie(festivitie.id).absoluteURL())
        }
    )
  }

  def editFestivitie(id: Int) = Action(parse.json) { implicit request =>
    request.body.validate[FestivitieData].fold(
      errors => BadRequest(errorJson(errors)),
      festivitieData =>
        if(!festivitieData.checkTime)
          BadRequest(errorJson("Fecha inicial debe estar antes de fecha final"))
        else
          Festivitie.find(id) match{
            case Some(festivitie) => festivitieData.update(festivitie.id); NoContent
            case None => NotFound(errorJson("The request resource could not be found"))
          }
    )
  }


  def searchFestivities(from: Option[DateTime], to: Option[DateTime]) = Action { implicit request =>
    val FestivitiesOrError = Festivitie.findFromAndTo(from, to)

    FestivitiesOrError fold(
      error => BadRequest(errorJson(error)),
      Festivities => {
        val FestivitiesJson = Json.obj("festivities" ->
          Festivities.map{ te =>
            val festivitieData = FestivitieData.fromFestivitie(te)
            addSelfLink(Json.toJson(festivitieData), routes.Festivities.getFestivitie(te.id))
          }
        )

        Ok(FestivitiesJson)
      }
      )
  }
}
