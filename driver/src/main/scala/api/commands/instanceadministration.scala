package reactivemongo.api.commands

object DropDatabase extends Command with CommandWithResult[UnitBox.type]

object Drop extends CollectionCommand with CommandWithResult[UnitBox.type]

object EmptyCapped extends CollectionCommand with CommandWithResult[UnitBox.type]

case class RenameCollection(
  fullyQualifiedCollectionName: String,
  fullyQualifiedTargetName: String,
  dropTarget: Boolean = false) extends Command with CommandWithResult[UnitBox.type]

case class Create(
  capped: Option[Capped] = None, // if set, "capped" -> true, size -> <int>, max -> <int>
  autoIndexId: Boolean = true, // optional
  flags: Int = 1 // defaults to 1
) extends CollectionCommand with CommandWithResult[UnitBox.type]

case class Capped(
  size: Long,
  max: Option[Int] = None
)

case class ConvertToCapped(
  capped: Capped) extends CollectionCommand with CommandWithResult[UnitBox.type]

case class CollStats(scale: Option[Int] = None) extends CollectionCommand with CommandWithResult[CollStatsResult]

/**
 * Various information about a collection.
 *
 * @param ns The fully qualified collection name.
 * @param count The number of documents in this collection.
 * @param size The size in bytes (or in bytes / scale, if any).
 * @param averageObjectSize The average object size in bytes (or in bytes / scale, if any).
 * @param storageSize Preallocated space for the collection.
 * @param numExtents Number of extents (contiguously allocated chunks of datafile space).
 * @param nindexes Number of indexes.
 * @param lastExtentSize Size of the most recently created extent.
 * @param paddingFactor Padding can speed up updates if documents grow.
 * @param systemFlags System flags.
 * @param userFlags User flags.
 * @param indexSizes Size of specific indexes in bytes.
 * @param capped States if this collection is capped.
 * @param max The maximum number of documents of this collection, if capped.
 */
case class CollStatsResult(
  ns: String,
  count: Int,
  size: Double,
  averageObjectSize: Option[Double],
  storageSize: Double,
  numExtents: Int,
  nindexes: Int,
  lastExtentSize: Int,
  paddingFactor: Double,
  systemFlags: Option[Int],
  userFlags: Option[Int],
  totalIndexSize: Int,
  indexSizes: Array[(String, Int)],
  capped: Boolean,
  max: Option[Long])

case class DropIndexes(index: String) extends CollectionCommand with CommandWithResult[DropIndexesResult]

case class DropIndexesResult(value: Int) extends BoxedAnyVal[Int]