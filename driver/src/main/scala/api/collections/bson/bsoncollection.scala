/*
 * Copyright 2012-2013 Stephane Godbillon (@sgodbillon) and Zenexity
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactivemongo.api.collections.bson

import reactivemongo.api.{ Collection, DB, FailoverStrategy, QueryOpts }
import reactivemongo.api.commands.bson._
import reactivemongo.api.collections.{ BatchCommands, GenericCollection, GenericCollectionProducer, GenericQueryBuilder }
import reactivemongo.api.BSONSerializationPack
import reactivemongo.bson._

object `package` {
  implicit object BSONCollectionProducer extends GenericCollectionProducer[BSONSerializationPack.type, BSONCollection] {
    def apply(db: DB, name: String, failoverStrategy: FailoverStrategy = FailoverStrategy()): BSONCollection =
      BSONCollection(db, name, failoverStrategy)
  }
}

object BSONBatchCommands extends BatchCommands[BSONSerializationPack.type] {
  val pack = BSONSerializationPack

  val InsertCommand = BSONInsertCommand
  implicit def InsertWriter = BSONInsertCommandImplicits.InsertWriter
  val UpdateCommand = BSONUpdateCommand
  implicit def UpdateWriter = BSONUpdateCommandImplicits.UpdateWriter
  implicit def UpdateReader = BSONUpdateCommandImplicits.UpdateResultReader
  val DeleteCommand = BSONDeleteCommand
  implicit def DeleteWriter = BSONDeleteCommandImplicits.DeleteWriter
  implicit def DefaultWriteResultReader = BSONCommonWriteCommandsImplicits.DefaultWriteResultReader

  implicit def LastErrorReader = BSONGetLastErrorImplicits.LastErrorReader
}

case class BSONCollection(val db: DB, val name: String, val failoverStrategy: FailoverStrategy) extends GenericCollection[BSONSerializationPack.type] {
  val pack = BSONSerializationPack
  val BatchCommands = BSONBatchCommands
  def genericQueryBuilder = BSONQueryBuilder(this, failoverStrategy)
}

case class BSONQueryBuilder(
  collection: Collection,
  failover: FailoverStrategy,
  queryOption: Option[BSONDocument] = None,
  sortOption: Option[BSONDocument] = None,
  projectionOption: Option[BSONDocument] = None,
  hintOption: Option[BSONDocument] = None,
  explainFlag: Boolean = false,
  snapshotFlag: Boolean = false,
  commentString: Option[String] = None,
  options: QueryOpts = QueryOpts()) extends GenericQueryBuilder[BSONSerializationPack.type] {
  import reactivemongo.utils.option

  type Self = BSONQueryBuilder
  val pack = BSONSerializationPack

  def copy(
    queryOption: Option[BSONDocument] = queryOption,
    sortOption: Option[BSONDocument] = sortOption,
    projectionOption: Option[BSONDocument] = projectionOption,
    hintOption: Option[BSONDocument] = hintOption,
    explainFlag: Boolean = explainFlag,
    snapshotFlag: Boolean = snapshotFlag,
    commentString: Option[String] = commentString,
    options: QueryOpts = options,
    failover: FailoverStrategy = failover): BSONQueryBuilder =
    BSONQueryBuilder(collection, failover, queryOption, sortOption, projectionOption, hintOption, explainFlag, snapshotFlag, commentString, options)

  def merge: BSONDocument =
    if (!sortOption.isDefined && !hintOption.isDefined && !explainFlag && !snapshotFlag && !commentString.isDefined)
      queryOption.getOrElse(BSONDocument())
    else
      BSONDocument(
        "$query" -> queryOption.getOrElse(BSONDocument()),
        "$orderby" -> sortOption,
        "$hint" -> hintOption,
        "$comment" -> commentString.map(BSONString(_)),
        "$explain" -> option(explainFlag, BSONBoolean(true)),
        "$snapshot" -> option(snapshotFlag, BSONBoolean(true)))

}