import uvicorn
from fastapi import FastAPI
from contextlib import asynccontextmanager
from apscheduler.schedulers.asyncio import AsyncIOScheduler # Async version eka
import py_eureka_client.eureka_client as eureka_client
from routes.sensor_routes import router as sensor_router
from services.sensor_service import fetch_external_telemetry

PORT = 8082

@asynccontextmanager
async def lifespan(app: FastAPI):
    try:
        await eureka_client.init_async(
            eureka_server="http://localhost:8761/eureka",
            app_name="sensor-service",
            instance_port=PORT
        )
    except Exception as e:
        print(f"Eureka connect une na: {e}")

    scheduler = AsyncIOScheduler()
    scheduler.add_job(fetch_external_telemetry, 'interval', seconds=10)
    scheduler.start()

    yield
    scheduler.shutdown()

app = FastAPI(title="Sensor Telemetry Service", lifespan=lifespan)
app.include_router(sensor_router, prefix="/api/sensors")

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=PORT)