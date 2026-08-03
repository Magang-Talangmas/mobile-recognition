import { useState } from "react";
import { Calendar, ChevronDown, Clock, Download, CheckCircle, XCircle, AlertCircle, MinusCircle } from "lucide-react";

const historyData = [
  { date: "Senin, 3 Agu 2026", records: [
    { name: "Budi Santoso", checkIn: "07:58", checkOut: "17:02", status: "tepat", avatar: "BS", color: "from-yellow-400 to-orange-400" },
    { name: "Sari Dewi", checkIn: "08:20", checkOut: "17:15", status: "terlambat", avatar: "SD", color: "from-pink-300 to-rose-400" },
    { name: "Andi Pratama", checkIn: "07:45", checkOut: "17:00", status: "tepat", avatar: "AP", color: "from-emerald-400 to-teal-400" },
    { name: "Rina Wahyu", checkIn: "08:00", checkOut: "-", status: "proses", avatar: "RW", color: "from-blue-300 to-sky-400" },
    { name: "Doni Kusuma", checkIn: "-", checkOut: "-", status: "izin", avatar: "DK", color: "from-orange-300 to-amber-400" },
  ]},
  { date: "Jumat, 1 Agu 2026", records: [
    { name: "Budi Santoso", checkIn: "08:05", checkOut: "17:10", status: "tepat", avatar: "BS", color: "from-yellow-400 to-orange-400" },
    { name: "Sari Dewi", checkIn: "08:00", checkOut: "17:00", status: "tepat", avatar: "SD", color: "from-pink-300 to-rose-400" },
    { name: "Andi Pratama", checkIn: "-", checkOut: "-", status: "alpha", avatar: "AP", color: "from-emerald-400 to-teal-400" },
    { name: "Rina Wahyu", checkIn: "07:55", checkOut: "17:05", status: "tepat", avatar: "RW", color: "from-blue-300 to-sky-400" },
  ]},
  { date: "Kamis, 31 Jul 2026", records: [
    { name: "Budi Santoso", checkIn: "08:15", checkOut: "17:20", status: "terlambat", avatar: "BS", color: "from-yellow-400 to-orange-400" },
    { name: "Sari Dewi", checkIn: "07:50", checkOut: "17:00", status: "tepat", avatar: "SD", color: "from-pink-300 to-rose-400" },
    { name: "Doni Kusuma", checkIn: "08:00", checkOut: "17:00", status: "tepat", avatar: "DK", color: "from-orange-300 to-amber-400" },
  ]},
];

const statusCfg = {
  tepat: { icon: CheckCircle, color: "#4ade80", bg: "rgba(74,222,128,0.12)", label: "Tepat Waktu" },
  terlambat: { icon: AlertCircle, color: "#FFB800", bg: "rgba(255,184,0,0.12)", label: "Terlambat" },
  izin: { icon: MinusCircle, color: "#60a5fa", bg: "rgba(96,165,250,0.12)", label: "Izin" },
  alpha: { icon: XCircle, color: "#f87171", bg: "rgba(248,113,113,0.12)", label: "Alpha" },
  proses: { icon: Clock, color: "#c084fc", bg: "rgba(192,132,252,0.12)", label: "Belum CO" },
};

const dotColor = (status: string) => {
  const map: Record<string, string> = { tepat: "#4ade80", terlambat: "#FFB800", izin: "#60a5fa", alpha: "#f87171", proses: "#c084fc" };
  return map[status] || "#fff";
};

export function HistoryScreen() {
  const [collapsed, setCollapsed] = useState<Record<string, boolean>>({});
  const toggleDay = (date: string) => setCollapsed((prev) => ({ ...prev, [date]: !prev[date] }));

  return (
    <div className="flex flex-col h-full" style={{ background: "#003399" }}>
      {/* Header */}
      <div
        className="px-5 pt-4 pb-4"
        style={{ background: "linear-gradient(180deg, #001f6e 0%, #003399 100%)" }}
      >
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-white" style={{ fontSize: "1.2rem", fontWeight: 700 }}>Riwayat Absensi</h1>
            <p className="text-white/50" style={{ fontSize: "0.8rem" }}>Agustus 2026</p>
          </div>
          <div className="flex gap-2">
            {[Calendar, Download].map((Icon, i) => (
              <button
                key={i}
                className="w-9 h-9 rounded-xl flex items-center justify-center"
                style={{ background: "rgba(255,255,255,0.1)", border: "1px solid rgba(255,255,255,0.12)" }}
              >
                <Icon className="w-4 h-4 text-white/60" />
              </button>
            ))}
          </div>
        </div>

        {/* Summary pills */}
        <div className="flex gap-2 overflow-x-auto pb-1">
          {[
            { label: "Hadir", val: "42", color: "#4ade80", bg: "rgba(74,222,128,0.12)", border: "rgba(74,222,128,0.25)" },
            { label: "Terlambat", val: "7", color: "#FFB800", bg: "rgba(255,184,0,0.12)", border: "rgba(255,184,0,0.25)" },
            { label: "Izin", val: "4", color: "#60a5fa", bg: "rgba(96,165,250,0.12)", border: "rgba(96,165,250,0.25)" },
            { label: "Alpha", val: "2", color: "#f87171", bg: "rgba(248,113,113,0.12)", border: "rgba(248,113,113,0.25)" },
          ].map((s) => (
            <div
              key={s.label}
              className="flex-shrink-0 px-3 py-2 rounded-xl text-center"
              style={{ background: s.bg, border: `1px solid ${s.border}` }}
            >
              <div style={{ fontSize: "1rem", fontWeight: 800, lineHeight: 1, color: s.color }}>{s.val}</div>
              <div style={{ fontSize: "0.65rem", fontWeight: 600, marginTop: "2px", color: s.color, opacity: 0.8 }}>{s.label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Days */}
      <div className="flex-1 overflow-y-auto px-5 pb-8 pt-3 space-y-4">
        {historyData.map((day) => {
          const isCollapsed = collapsed[day.date];
          const presentCount = day.records.filter((r) => r.status !== "izin" && r.status !== "alpha").length;

          return (
            <div
              key={day.date}
              className="rounded-2xl overflow-hidden"
              style={{ background: "rgba(255,255,255,0.07)", border: "1px solid rgba(255,255,255,0.1)" }}
            >
              <button
                className="w-full flex items-center justify-between px-4 py-3.5 transition-opacity active:opacity-70"
                onClick={() => toggleDay(day.date)}
              >
                <div>
                  <div className="text-white text-left" style={{ fontSize: "0.875rem", fontWeight: 700 }}>{day.date}</div>
                  <div className="text-white/50 text-left" style={{ fontSize: "0.75rem" }}>{presentCount}/{day.records.length} hadir</div>
                </div>
                <div className="flex items-center gap-2">
                  <div className="flex gap-1">
                    {day.records.map((r, i) => (
                      <div key={i} className="w-2 h-2 rounded-full" style={{ backgroundColor: dotColor(r.status) }} />
                    ))}
                  </div>
                  <ChevronDown className={`w-4 h-4 text-white/30 transition-transform ${isCollapsed ? "-rotate-90" : ""}`} />
                </div>
              </button>

              {!isCollapsed && (
                <div className="border-t divide-y" style={{ borderColor: "rgba(255,255,255,0.08)" }}>
                  {day.records.map((rec, i) => {
                    const cfg = statusCfg[rec.status as keyof typeof statusCfg];
                    return (
                      <div key={i} className="flex items-center gap-3 px-4 py-3" style={{ borderColor: "rgba(255,255,255,0.06)" }}>
                        <div className={`w-9 h-9 rounded-xl bg-gradient-to-br ${rec.color} flex items-center justify-center flex-shrink-0`}>
                          <span className="text-white" style={{ fontSize: "0.65rem", fontWeight: 700 }}>{rec.avatar}</span>
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="text-white" style={{ fontSize: "0.825rem", fontWeight: 600 }}>{rec.name}</div>
                          <div className="flex items-center gap-2 mt-0.5">
                            <span className="text-white/40" style={{ fontSize: "0.7rem" }}>CI: <span className="text-white/70">{rec.checkIn}</span></span>
                            <span className="text-white/20">·</span>
                            <span className="text-white/40" style={{ fontSize: "0.7rem" }}>CO: <span className="text-white/70">{rec.checkOut}</span></span>
                          </div>
                        </div>
                        <div className="flex items-center gap-1 px-2 py-1 rounded-lg" style={{ background: cfg.bg }}>
                          <cfg.icon className="w-3 h-3" style={{ color: cfg.color }} />
                          <span style={{ fontSize: "0.65rem", fontWeight: 600, color: cfg.color }}>{cfg.label}</span>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
