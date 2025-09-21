from fastapi import FastAPI
import uvicorn
import requests
import pandas as pd
import numpy as np

from datetime import datetime
from typing import Optional

app = FastAPI(title="Python Service", description="Python service in Bazel monorepo")


@app.get("/")
def read_root():
    return {
        "message": "Hello from a FastAPI app built with Bazel!",
        "service": "python-service",
        "timestamp": datetime.now().isoformat(),
    }


@app.get("/health")
def health_check():
    return {
        "status": "healthy",
        "service": "python-service",
        "version": "1.0.0",
        "timestamp": datetime.now().isoformat(),
    }


@app.get("/items/{item_id}")
def read_item(item_id: int, q: Optional[str] = None):
    return {
        "item_id": item_id,
        "q": q,
        "service": "python-service",
        "timestamp": datetime.now().isoformat(),
    }


@app.get("/data/analysis")
def data_analysis():
    """Demonstrate data analysis capabilities using pandas/numpy"""
    # Create sample data
    data = {
        "product": ["Laptop", "Phone", "Tablet", "Monitor"],
        "sales": np.random.randint(100, 1000, 4),
        "profit": np.random.uniform(0.1, 0.5, 4),
    }

    df = pd.DataFrame(data)

    return {
        "message": "Data analysis from Python service",
        "total_sales": int(df["sales"].sum()),
        "average_profit": float(df["profit"].mean()),
        "top_product": df.loc[df["sales"].idxmax(), "product"],
        "data": df.to_dict("records"),
        "timestamp": datetime.now().isoformat(),
    }


@app.get("/call-kotlin")
def call_kotlin_service():
    """Cross-service communication: call Kotlin service"""
    try:
        response = requests.get("http://localhost:8080/health", timeout=5)
        kotlin_data = response.json()

        return {
            "message": "Successfully called Kotlin service from Python",
            "python_service": "running",
            "kotlin_response": kotlin_data,
            "timestamp": datetime.now().isoformat(),
        }
    except requests.exceptions.RequestException as e:
        return {
            "message": "Failed to call Kotlin service",
            "error": str(e),
            "python_service": "running",
            "timestamp": datetime.now().isoformat(),
        }


@app.get("/shared-models/user/{user_id}")
def get_user_shared_format(user_id: int):
    """Return user data in the same format as Kotlin service for consistency"""
    return {
        "success": True,
        "data": {
            "id": user_id,
            "name": f"Python User {user_id}",
            "email": f"python.user{user_id}@example.com",
            "createdAt": datetime.now().isoformat(),
        },
        "message": "User data from Python service",
        "timestamp": datetime.now().isoformat(),
    }


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
