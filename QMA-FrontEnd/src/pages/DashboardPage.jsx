import { useState, useEffect } from "react";
import toast from "react-hot-toast";
import { convert, compare, add, subtract, divide } from "../api/api";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const UNIT_MAP = {
  LENGTH: ["INCH", "FEET", "YARD", "CENTIMETER"],
  VOLUME: ["GALLON", "LITRE", "MILLILITER"],
  WEIGHT: ["GRAM", "KILOGRAM", "TONNE"],
  TEMPERATURE: ["CELSIUS", "FAHRENHEIT"],
};

const TYPE_ICONS = {
  LENGTH: "📏",
  WEIGHT: "⚖️",
  TEMPERATURE: "🌡️",
  VOLUME: "🧪",
};

const OPERATIONS = [
  { value: "COMPARE", label: "Comparison" },
  { value: "CONVERT", label: "Conversion" },
  { value: "ADD", label: "Add" },
  { value: "SUBTRACT", label: "Subtract" },
  { value: "DIVIDE", label: "Divide" },
];

function formatUnit(unit) {
  return unit.charAt(0) + unit.slice(1).toLowerCase();
}

export default function DashboardPage() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const [operation, setOperation] = useState("CONVERT");
  const [quantityType, setQuantityType] = useState("LENGTH");
  const [value1, setValue1] = useState("");
  const [unit1, setUnit1] = useState("INCH");
  const [value2, setValue2] = useState("");
  const [unit2, setUnit2] = useState("FEET");
  const [targetUnit, setTargetUnit] = useState("FEET");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const availableUnits = UNIT_MAP[quantityType];

  useEffect(() => {
    setUnit1(availableUnits[0]);
    setUnit2(availableUnits.length > 1 ? availableUnits[1] : availableUnits[0]);
    setTargetUnit(availableUnits.length > 1 ? availableUnits[1] : availableUnits[0]);
    setResult(null);
  }, [quantityType]);

  function handleTypeSelect(type) {
    setQuantityType(type);
  }

  function handleOperationSelect(op) {
    setOperation(op);
    setResult(null);
  }

  function getButtonLabel() {
    if (operation === "CONVERT") return "Convert";
    if (operation === "COMPARE") return "Compare";
    if (operation === "ADD") return "Add";
    if (operation === "SUBTRACT") return "Subtract";
    if (operation === "DIVIDE") return "Divide";
    return operation;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setResult(null);

    try {
      if (operation === "CONVERT") {
        const data = await convert(quantityType, parseFloat(value1), unit1, targetUnit);
        const outputUnit = data.targetUnitStr || data.targetUnit || data.unit || targetUnit;
        setResult({ text: `${data.resultValue.toFixed(2)} ${formatUnit(outputUnit)}`, type: "success" });
      } else if (operation === "COMPARE") {
        const data = await compare(quantityType, parseFloat(value1), unit1, parseFloat(value2), unit2);
        const isEqual = data === true || data === "true";
        setResult({ text: isEqual ? "EQUAL" : "NOT EQUAL", type: isEqual ? "success" : "danger" });
      } else if (operation === "ADD") {
        const data = await add(quantityType, parseFloat(value1), unit1, parseFloat(value2), unit2, targetUnit);
        const outputUnit = data.targetUnitStr || data.targetUnit || data.unit || targetUnit;
        setResult({ text: `${data.resultValue.toFixed(2)} ${formatUnit(outputUnit)}`, type: "success" });
      } else if (operation === "SUBTRACT") {
        const data = await subtract(quantityType, parseFloat(value1), unit1, parseFloat(value2), unit2, targetUnit);
        const outputUnit = data.targetUnitStr || data.targetUnit || data.unit || targetUnit;
        setResult({ text: `${data.resultValue.toFixed(2)} ${formatUnit(outputUnit)}`, type: "success" });
      } else if (operation === "DIVIDE") {
        const data = await divide(quantityType, parseFloat(value1), unit1, parseFloat(value2), unit2, targetUnit);
        const outputUnit = data.targetUnitStr || data.targetUnit || data.unit || targetUnit;
        setResult({ text: `${data.resultValue.toFixed(2)} ${formatUnit(outputUnit)}`, type: "success" });
      }
      toast.success("Operation successful!");
    } catch (err) {
      toast.error(err.message);
      if (err.message.includes("Session expired")) {
        logout();
        navigate("/login");
      }
    } finally {
      setLoading(false);
    }
  }

  const showValue2 = operation === "COMPARE" || operation === "ADD" || operation === "SUBTRACT" || operation === "DIVIDE";
  const showTargetUnit = operation === "CONVERT" || operation === "ADD" || operation === "SUBTRACT" || operation === "DIVIDE";

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-lg-9">

          {/* CHOOSE TYPE */}
          <h6 className="text-uppercase text-muted fw-bold small mb-3">Choose Type</h6>
          <div className="row row-cols-2 row-cols-md-4 g-3 mb-4">
            {Object.keys(UNIT_MAP).map((type) => (
              <div className="col" key={type}>
                <div
                  className={`card text-center py-3 border-2 type-card ${quantityType === type ? "active" : ""}`}
                  onClick={() => handleTypeSelect(type)}
                >
                  <div className="type-icon">{TYPE_ICONS[type]}</div>
                  <div className="fw-semibold mt-1">{formatUnit(type)}</div>
                </div>
              </div>
            ))}
          </div>

          {/* CHOOSE ACTION */}
          <h6 className="text-uppercase text-muted fw-bold small mb-3">Choose Action</h6>
          <div className="btn-group w-100 mb-4" role="group">
            {OPERATIONS.map((op) => (
              <button
                key={op.value}
                type="button"
                className={`btn ${operation === op.value ? "btn-primary" : "btn-outline-secondary"}`}
                onClick={() => handleOperationSelect(op.value)}
              >
                {op.label}
              </button>
            ))}
          </div>

          {/* FROM / TO */}
          <form onSubmit={handleSubmit} id="conversionForm">
            <div className="row g-3 mb-4">
              {/* FROM card */}
              <div className="col-md-6">
                <div className="card shadow-sm">
                  <div className="card-body">
                    <h6 className="text-uppercase text-muted fw-bold small mb-2">From</h6>
                    <input
                      type="number"
                      className="form-control value-input mb-3"
                      id="inputValue1"
                      placeholder="0"
                      step="0.01"
                      value={value1}
                      onChange={(e) => setValue1(e.target.value)}
                      required
                    />
                    <select
                      className="form-select"
                      id="inputUnit1"
                      value={unit1}
                      onChange={(e) => setUnit1(e.target.value)}
                    >
                      {availableUnits.map((u) => (
                        <option key={u} value={u}>{formatUnit(u)}</option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>

              {/* TO / Second Value card */}
              <div className="col-md-6">
                <div className="card shadow-sm">
                  <div className="card-body">
                    <h6 className="text-uppercase text-muted fw-bold small mb-2">
                      {showValue2 ? "Second Value" : "To"}
                    </h6>
                    {showValue2 ? (
                      <>
                        <input
                          type="number"
                          className="form-control value-input mb-3"
                          id="inputValue2"
                          placeholder="0"
                          step="0.01"
                          value={value2}
                          onChange={(e) => setValue2(e.target.value)}
                          required
                        />
                        <select
                          className="form-select"
                          id="inputUnit2"
                          value={unit2}
                          onChange={(e) => setUnit2(e.target.value)}
                        >
                          {availableUnits.map((u) => (
                            <option key={u} value={u}>{formatUnit(u)}</option>
                          ))}
                        </select>
                      </>
                    ) : (
                      <>
                        <div className="value-input text-muted mb-3">—</div>
                        <select
                          className="form-select"
                          id="targetUnit"
                          value={targetUnit}
                          onChange={(e) => setTargetUnit(e.target.value)}
                        >
                          {availableUnits.map((u) => (
                            <option key={u} value={u}>{formatUnit(u)}</option>
                          ))}
                        </select>
                      </>
                    )}
                  </div>
                </div>
              </div>
            </div>

            {/* Result Unit for ADD */}
            {operation === "ADD" && (
              <div className="card shadow-sm mb-4">
                <div className="card-body">
                  <h6 className="text-uppercase text-muted fw-bold small mb-2">Result Unit</h6>
                  <select
                    className="form-select"
                    id="targetUnit"
                    value={targetUnit}
                    onChange={(e) => setTargetUnit(e.target.value)}
                  >
                    {availableUnits.map((u) => (
                      <option key={u} value={u}>{formatUnit(u)}</option>
                    ))}
                  </select>
                </div>
              </div>
            )}

            {/* Submit */}
            <button type="submit" className="btn btn-primary btn-lg w-100" disabled={loading} id="submitBtn">
              {loading && (
                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              )}
              {loading ? "Processing..." : getButtonLabel()}
            </button>
          </form>

          {/* Result */}
          {result && (
            <div className={`alert alert-${result.type} mt-4 text-center`} role="alert" id="resultDisplay">
              <h5 className="alert-heading">Result</h5>
              <p className="mb-0 fs-2 fw-bold" id="finalResult">{result.text}</p>
            </div>
          )}

        </div>
      </div>
    </div>
  );
}
