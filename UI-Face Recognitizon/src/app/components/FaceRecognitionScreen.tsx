import { useState, useEffect } from "react";
import { motion } from "motion/react";
import { CheckCircle, XCircle, RotateCcw, ChevronLeft } from "lucide-react";

type ScanState = "idle" | "scanning" | "success" | "failed";

const mockEmployees = [
  { name: "Budi Santoso", dept: "Engineering", id: "EMP-001", avatar: "BS" },
  { name: "Sari Dewi", dept: "Marketing", id: "EMP-007", avatar: "SD" },
  { name: "Andi Pratama", dept: "Finance", id: "EMP-013", avatar: "AP" },
];

interface Props {
  onBack: () => void;
  logo: string;
}

export function FaceRecognitionScreen({ onBack, logo }: Props) {
  const [scanState, setScanState] = useState<ScanState>("idle");
  const [detectedEmployee, setDetectedEmployee] = useState<typeof mockEmployees[0] | null>(null);
  const [scanProgress, setScanProgress] = useState(0);
  const [attendanceType, setAttendanceType] = useState<"in" | "out">("in");

  useEffect(() => {
    let timer: ReturnType<typeof setTimeout>;
    let progressInterval: ReturnType<typeof setInterval>;

    if (scanState === "scanning") {
      setScanProgress(0);
      progressInterval = setInterval(() => {
        setScanProgress((p) => Math.min(p + 2.5, 100));
      }, 50);
      timer = setTimeout(() => {
        clearInterval(progressInterval);
        const success = Math.random() > 0.2;
        if (success) {
          const emp = mockEmployees[Math.floor(Math.random() * mockEmployees.length)];
          setDetectedEmployee(emp);
          setScanState("success");
        } else {
          setScanState("failed");
        }
      }, 2000);
    }

    return () => {
      clearTimeout(timer);
      clearInterval(progressInterval);
    };
  }, [scanState]);

  const reset = () => {
    setScanState("idle");
    setDetectedEmployee(null);
    setScanProgress(0);
  };

  const cornerColor = scanState === "success" ? "#4ade80" : scanState === "failed" ? "#f87171" : "#FFB800";

  return (
    <div className="flex flex-col h-full overflow-hidden" style={{ background: "#003399" }}>
      {/* Top Bar */}
      <div
        className="flex items-center gap-3 px-5 pt-4 pb-4"
        style={{ background: "linear-gradient(180deg, #001f6e 0%, #003399 100%)" }}
      >
        <button
          onClick={onBack}
          className="w-9 h-9 rounded-full flex items-center justify-center"
          style={{ background: "rgba(255,255,255,0.12)", border: "1px solid rgba(255,255,255,0.15)" }}
        >
          <ChevronLeft className="w-5 h-5 text-white" />
        </button>
        <div className="flex items-center gap-2.5 flex-1">
          <div className="w-8 h-8 rounded-lg overflow-hidden bg-white flex items-center justify-center flex-shrink-0">
            <img src={logo} alt="Logo" className="w-full h-full object-contain" />
          </div>
          <div>
            <h1 className="text-white" style={{ fontSize: "1rem", fontWeight: 700 }}>Face Recognition</h1>
            <p className="text-white/50" style={{ fontSize: "0.7rem" }}>Arahkan wajah ke kamera</p>
          </div>
        </div>
      </div>

      {/* Type Toggle */}
      <div className="mx-5 mb-4 mt-2">
        <div className="flex rounded-xl p-1" style={{ background: "rgba(0,0,0,0.2)", border: "1px solid rgba(255,255,255,0.1)" }}>
          {(["in", "out"] as const).map((type) => (
            <button
              key={type}
              onClick={() => { setAttendanceType(type); reset(); }}
              className="flex-1 py-2 rounded-lg transition-all"
              style={{
                background: attendanceType === type ? "linear-gradient(135deg, #FFB800, #FF8C00)" : "transparent",
                color: attendanceType === type ? "white" : "rgba(255,255,255,0.45)",
                fontSize: "0.85rem",
                fontWeight: 600,
                boxShadow: attendanceType === type ? "0 4px 12px rgba(255,184,0,0.3)" : "none",
              }}
            >
              {type === "in" ? "Check In" : "Check Out"}
            </button>
          ))}
        </div>
      </div>

      {/* Camera Viewport */}
      <div className="flex-1 flex flex-col items-center justify-center px-5">
        <div className="relative w-full rounded-3xl overflow-hidden" style={{ aspectRatio: "3/4", maxHeight: "380px" }}>
          {/* Camera background gradient */}
          <div
            className="absolute inset-0"
            style={{ background: "linear-gradient(160deg, #001240 0%, #001f6e 50%, #002080 100%)" }}
          />

          {/* Grid dot pattern */}
          <div
            className="absolute inset-0 pointer-events-none"
            style={{
              backgroundImage: "radial-gradient(circle, rgba(255,255,255,0.04) 1px, transparent 1px)",
              backgroundSize: "22px 22px",
            }}
          />

          {/* Silhouette */}
          <div className="absolute inset-0 flex items-center justify-center">
            <svg width="160" height="200" viewBox="0 0 180 220" fill="none" opacity={scanState === "idle" ? 0.12 : 0.06}>
              <ellipse cx="90" cy="72" rx="44" ry="50" fill="white" />
              <path d="M10 220 C10 160 50 140 90 140 C130 140 170 160 170 220" fill="white" />
            </svg>
          </div>

          {/* Face frame */}
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="relative w-52 h-64">
              {[
                "top-0 left-0 border-t-2 border-l-2 rounded-tl-2xl",
                "top-0 right-0 border-t-2 border-r-2 rounded-tr-2xl",
                "bottom-0 left-0 border-b-2 border-l-2 rounded-bl-2xl",
                "bottom-0 right-0 border-b-2 border-r-2 rounded-br-2xl",
              ].map((cls, i) => (
                <div
                  key={i}
                  className={`absolute w-8 h-8 ${cls} transition-colors duration-300`}
                  style={{ borderColor: cornerColor }}
                />
              ))}

              {/* Scan line */}
              {scanState === "scanning" && (
                <motion.div
                  className="absolute left-0 right-0 h-0.5"
                  style={{ background: "linear-gradient(90deg, transparent, #FFB800, transparent)" }}
                  initial={{ top: 0 }}
                  animate={{ top: "100%" }}
                  transition={{ duration: 1.8, ease: "linear", repeat: Infinity }}
                />
              )}

              {/* Result overlay */}
              {scanState === "success" && (
                <motion.div
                  initial={{ opacity: 0, scale: 0.8 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="absolute inset-0 flex items-center justify-center"
                >
                  <div
                    className="w-16 h-16 rounded-full flex items-center justify-center"
                    style={{ background: "rgba(74,222,128,0.15)", border: "2px solid #4ade80", backdropFilter: "blur(8px)" }}
                  >
                    <CheckCircle className="w-8 h-8" style={{ color: "#4ade80" }} />
                  </div>
                </motion.div>
              )}
              {scanState === "failed" && (
                <motion.div
                  initial={{ opacity: 0, scale: 0.8 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="absolute inset-0 flex items-center justify-center"
                >
                  <div
                    className="w-16 h-16 rounded-full flex items-center justify-center"
                    style={{ background: "rgba(248,113,113,0.15)", border: "2px solid #f87171", backdropFilter: "blur(8px)" }}
                  >
                    <XCircle className="w-8 h-8" style={{ color: "#f87171" }} />
                  </div>
                </motion.div>
              )}
            </div>
          </div>

          {/* Progress */}
          {scanState === "scanning" && (
            <div className="absolute bottom-4 left-6 right-6">
              <div className="h-1 rounded-full overflow-hidden" style={{ background: "rgba(255,255,255,0.1)" }}>
                <div
                  className="h-full rounded-full transition-all"
                  style={{
                    width: `${scanProgress}%`,
                    background: "linear-gradient(90deg, #FFB800, #FF8C00)",
                  }}
                />
              </div>
              <p className="text-center mt-2" style={{ fontSize: "0.75rem", fontWeight: 600, color: "#FFB800" }}>
                Memindai... {Math.round(scanProgress)}%
              </p>
            </div>
          )}
        </div>

        {/* Status text */}
        <div className="mt-5 text-center px-4">
          {scanState === "idle" && (
            <>
              <p className="text-white/60" style={{ fontSize: "0.85rem" }}>Posisikan wajah di dalam bingkai</p>
              <p className="text-white/35 mt-1" style={{ fontSize: "0.75rem" }}>Pastikan pencahayaan cukup</p>
            </>
          )}
          {scanState === "scanning" && (
            <p style={{ color: "#FFB800", fontSize: "0.85rem", fontWeight: 600 }}>Mengenali wajah Anda...</p>
          )}
          {scanState === "success" && detectedEmployee && (
            <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}>
              <p style={{ color: "#4ade80", fontSize: "0.9rem", fontWeight: 700 }}>Wajah Dikenali!</p>
              <div
                className="mt-2 inline-block px-5 py-3 rounded-2xl"
                style={{ background: "rgba(255,255,255,0.08)", border: "1px solid rgba(74,222,128,0.25)" }}
              >
                <div className="text-white" style={{ fontSize: "1rem", fontWeight: 700 }}>{detectedEmployee.name}</div>
                <div className="text-white/50" style={{ fontSize: "0.75rem" }}>{detectedEmployee.dept} · {detectedEmployee.id}</div>
                <div style={{ color: "#4ade80", fontSize: "0.8rem", fontWeight: 600, marginTop: "4px" }}>
                  {attendanceType === "in" ? "✓ Check In berhasil" : "✓ Check Out berhasil"}
                </div>
              </div>
            </motion.div>
          )}
          {scanState === "failed" && (
            <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}>
              <p style={{ color: "#f87171", fontSize: "0.9rem", fontWeight: 700 }}>Wajah Tidak Dikenali</p>
              <p className="text-white/50 mt-1" style={{ fontSize: "0.8rem" }}>Coba lagi atau hubungi admin</p>
            </motion.div>
          )}
        </div>
      </div>

      {/* Action Button */}
      <div className="px-5 pb-10 pt-4 space-y-3">
        {(scanState === "idle" || scanState === "scanning") && (
          <button
            onClick={() => setScanState("scanning")}
            disabled={scanState === "scanning"}
            className="w-full py-4 rounded-2xl text-white transition-all active:scale-95 disabled:opacity-70"
            style={{
              background: "linear-gradient(135deg, #FFB800, #FF8C00)",
              fontSize: "0.95rem",
              fontWeight: 700,
              boxShadow: "0 8px 24px rgba(255,184,0,0.35)",
            }}
          >
            {scanState === "scanning" ? "Memindai..." : "Mulai Scan"}
          </button>
        )}
        {(scanState === "success" || scanState === "failed") && (
          <button
            onClick={reset}
            className="w-full py-4 rounded-2xl flex items-center justify-center gap-2 text-white transition-all active:scale-95"
            style={{
              background: "rgba(255,255,255,0.1)",
              border: "1px solid rgba(255,255,255,0.15)",
              fontSize: "0.95rem",
              fontWeight: 600,
            }}
          >
            <RotateCcw className="w-4 h-4" /> Scan Lagi
          </button>
        )}
      </div>
    </div>
  );
}
