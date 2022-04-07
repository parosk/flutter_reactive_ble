package com.signify.hue.flutterreactiveble.converters

import android.util.SparseArray

fun extractManufacturerData(
    manufacturerData: SparseArray<ByteArray>?,
    scanRecordBytes: ByteArray?
): ByteArray {
    val rawData = mutableListOf<Byte>()
    val scanRecordBytesList = scanRecordBytes?.toList()
    if (manufacturerData != null && manufacturerData.size() > 0) {
        val companyId = manufacturerData.keyAt(0)
        rawData.add((companyId.toByte()))
        rawData.add(((companyId.shr(Byte.SIZE_BITS)).toByte()))
        scanRecordBytes?.forEachIndexed { i, byteValue ->
            // if next byte is 0xFF and the following 2 bytes is companyId,
            // then current byte value is the length of the Manufacturer Specific data
            // the following data are Manufacturer Specific data
            if (scanRecordBytes.size > i + 3 && scanRecordBytes[i + 1] == 0xFF.toByte() &&
                scanRecordBytes[i + 2] == companyId.toByte() && scanRecordBytes[i + 3] == (companyId.shr(
                    Byte.SIZE_BITS
                )).toByte()
            ) {
                val manufacturerDataLength = byteValue.toInt()
                if (scanRecordBytes.size > i + manufacturerDataLength) {
                    scanRecordBytesList?.subList(i + 4, i + manufacturerDataLength + 1)
                        ?.let { rawData.addAll(it) }
                }
            }
        }
    }
    return rawData.toByteArray()
}
