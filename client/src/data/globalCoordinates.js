import {
	ensureCoordinateDatasetReady,
	ensureLocationCoordinate,
	getAllLocationNames,
	getExactCoordinate,
	resolveLocationCoordinate
} from '@/services/coordinateProvider'

if (typeof console !== 'undefined' && typeof console.warn === 'function') {
	console.warn('[globalCoordinates] Deprecated import – switch to services/coordinateProvider for coordinate lookups.')
}

ensureCoordinateDatasetReady()

export {
	ensureCoordinateDatasetReady,
	ensureLocationCoordinate,
	getAllLocationNames,
	getExactCoordinate,
	resolveLocationCoordinate
}

export default {
	ensureCoordinateDatasetReady,
	ensureLocationCoordinate,
	getAllLocationNames,
	getExactCoordinate,
	resolveLocationCoordinate
}
