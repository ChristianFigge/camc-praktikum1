import json
import os
import zlib
from enum import StrEnum
from typing import Type


class SensorType(StrEnum):
    Accelerometer = "Accelerometer"
    Gravity = "Gravity"
    LinearAccel = "LinearAccel"
    Gyroscope = "Gyroscope"
    MagneticField = "MagneticField"
    Rotation = "Rotation"
    Orientation = "Orientation"


class EventMode(StrEnum):
    Stillstand = "Stillstand"
    Gehen = "Gehen"
    Joggen = "Joggen"
    Rennen = "Rennen"
    Fahrrad = "Fahrrad"
    Auto = "Auto"
    Bus = "Bus"
    Bahn = "Bahn"
    SYNC = "SYNC"


class SensorEvent:
    def __init__(self, data: dict):
        if type(data) is not dict:
            raise TypeError("Parameter 'data' has the wrong type (expected dict, got", type(data))
        if set(data.keys()) != {"values", "timestampMillis", "mode"}:
            raise ValueError("Parameter 'data' has the wrong dict format")

        self.values: list[float] = data["values"]
        self.timestampMs: int = data["timestampMillis"]
        self.mode: Type[EventMode] = EventMode[data["mode"]]

    def __str__(self) -> str:
        """CSV mit value1,value2,...,timestamp,mode"""
        return f"{self.values[0]},{self.values[1]},{self.values[2]},{self.timestampMs},{self.mode}"


class SensorRecordSession:
    def __init__(self, data: dict):
        if type(data) is not dict:
            raise TypeError("Parameter 'data' has the wrong type (expected dict, got", type(data))
        if len(data["sensorTypes"]) != len(data["sensorRecordings"]):
            raise ValueError("Parameter 'data' has the wrong dict format")

        self.sensor_types = [SensorType[x] for x in data["sensorTypes"]]
        self.data: dict[Type[SensorType] | str, list[SensorEvent]] \
            = {t:list() for t in self.sensor_types}

        # fill data dict with SensorEvent objects
        for sensor_type, data in zip(self.sensor_types, data["sensorRecordings"]):
            for event in data:
                self.data[sensor_type].append(SensorEvent(event))

            # overwriting SYNC events
            # assuming cloning the nearest point is safer than extrapolating
            if self.data[sensor_type][0].mode == "SYNC":
                self.data[sensor_type][0].values = self.data[sensor_type][1].values.copy()
                self.data[sensor_type][0].mode = self.data[sensor_type][1].mode
            if self.data[sensor_type][-1].mode == "SYNC":
                self.data[sensor_type][-1].values = self.data[sensor_type][-2].values.copy()
                self.data[sensor_type][-1].mode = self.data[sensor_type][-2].mode

    def __len__(self):
        num_of_events = 0
        for rec in self.data.values():
            num_of_events += len(rec)
        return num_of_events

    @classmethod
    def from_compressed_file(cls, file_path):
        compressed_data = open(file_path, 'rb').read()
        decompressed_data = zlib.decompress(compressed_data)
        data_dict = json.loads(decompressed_data)
        return cls(data_dict)


if __name__ == "__main__":
    data_folder = "."
    file_name = "example.json.zlib"

    file_path = os.path.join(data_folder, file_name)
    print(f"\nImporting & decompressing file '{file_path}' ...\n")

    sess = SensorRecordSession.from_compressed_file(file_path)
    print(f"Import successful! Created object {type(sess)}")

    print(f"It holds a total of {len(sess)} events from sensors {[str(x) for x in sess.sensor_types]}\n")
    sensor = sess.sensor_types[0]
    print(f"The first and last 5 SensorEvents from {sensor} as CSV:")
    for event in sess.data[sensor][:5]:
        print(event)
    print("...")
    for event in sess.data[sensor][-5:]:
        print(event)
