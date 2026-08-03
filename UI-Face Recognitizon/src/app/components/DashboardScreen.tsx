import { useState } from "react";
import {
  Clock,
  CheckCircle,
  XCircle,
  TrendingUp,
  Bell,
  Calendar,
  Users,
  ChevronRight,
  MapPin,
} from "lucide-react";

const recentActivity = [
  { name: "Budi Santoso", action: "Check In", time: "08:02", status: "on-time", avatar: "BS" },
  { name: "Sari Dewi", action: "Check In", time: "08:15", status: "late", avatar: "SD" },
  { name: "Andi Pratama", action: "Check Out", time: "17:05", status: "on-time", avatar: "AP" },
  { name: "Rina Wahyu", action: "Check In", time: "07:55", status: "on-time", avatar: "RW" },
];

const avatarColors = [
  "from-yellow-400 to-orange-400",
  "from-blue-300 to-cyan-300",
  "from-emerald-400 to-teal-400",
  "from-pink-400 to-rose-400",
];

interface Props {
  onScanPress: () => void;
  logo: string;
}

export function DashboardScreen({ onScanPress, logo }: Props) {
  const [notifCount] = useState(3);
  const now = new Date();
  const timeStr = now.toLocaleTimeString("id-ID", { hour: "2-digit", minute: "2-digit" });
  const dateStr = now.toLocaleDateString("id-ID", { weekday: "long", day: "numeric", month: "long", year: "numeric" });

  return (
    <div className="flex flex-col h-full overflow-y-auto" style={{ background: "#003399" }}>
      {/* Header */}
      <div
        className="relative px-5 pt-4 pb-6"
        style={{
          background: "linear-gradient(160deg, #001f6e 0%, #003399 60%, #0044cc 100%)",
        }}
      >
        {/* Subtle circle decorations */}
        <div className="absolute top-0 right-0 w-56 h-56 rounded-full opacity-10" style={{ background: "#FFB800", filter: "blur(60px)", transform: "translate(30%, -30%)" }} />
        <div className="absolute bottom-0 left-0 w-40 h-40 rounded-full opacity-8" style={{ background: "#ffffff", filter: "blur(50px)", transform: "translate(-20%, 20%)" }} />

        {/* Top bar: Logo + Greeting + Bell */}
        <div className="relative flex items-center justify-between mb-5">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl overflow-hidden bg-white flex items-center justify-center shadow-lg" style={{ boxShadow: "0 4px 14px rgba(0,0,0,0.25)" }}>
              <img src={logo} alt="Talangmas" className="w-full h-full object-contain" />
            </div>
            <div>
              <p className="text-white/60" style={{ fontSize: "0.7rem" }}>Selamat pagi,</p>
              <h1 className="text-white" style={{ fontSize: "1rem", fontWeight: 700, lineHeight: 1.2 }}>Admin HR 👋</h1>
            </div>
          </div>
          <button className="relative w-10 h-10 rounded-full flex items-center justify-center border" style={{ background: "rgba(255,255,255,0.1)", borderColor: "rgba(255,255,255,0.2)" }}>
            <Bell className="w-5 h-5 text-white" />
            {notifCount > 0 && (
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center text-white" style={{ fontSize: "0.65rem", fontWeight: 700 }}>
                {notifCount}
              </span>
            )}
          </button>
        </div>

        {/* Live Clock card */}
        <div
          className="relative rounded-2xl p-5 overflow-hidden"
          style={{
            background: "rgba(255,255,255,0.08)",
            border: "1px solid rgba(255,255,255,0.15)",
            backdropFilter: "blur(12px)",
          }}
        >
          {/* Gold accent line */}
          <div className="absolute top-0 left-0 right-0 h-0.5 rounded-t-2xl" style={{ background: "linear-gradient(90deg, #FFB800, #FF8C00)" }} />
          <div className="flex items-center gap-2 mb-1">
            <div className="w-2 h-2 rounded-full animate-pulse" style={{ background: "#4ade80" }} />
            <span style={{ color: "#4ade80", fontSize: "0.7rem", fontWeight: 600, letterSpacing: "0.08em" }}>LIVE</span>
          </div>
          <div className="text-white" style={{ fontSize: "2.4rem", fontWeight: 800, letterSpacing: "-1px", lineHeight: 1 }}>{timeStr}</div>
          <div className="flex items-center gap-1.5 mt-2">
            <Calendar className="w-3.5 h-3.5 text-white/50" />
            <span className="text-white/60 capitalize" style={{ fontSize: "0.78rem" }}>{dateStr}</span>
          </div>
          <div className="flex items-center gap-1.5 mt-1">
            <MapPin className="w-3.5 h-3.5 text-white/50" />
            <span className="text-white/60" style={{ fontSize: "0.78rem" }}>Kantor Talangmas, Jakarta</span>
          </div>
        </div>
      </div>

      <div className="flex-1 px-5 pb-8 space-y-5" style={{ background: "#003399" }}>
        {/* Stats Grid */}
        <div className="grid grid-cols-2 gap-3 pt-1">
          {[
            { label: "Hadir Hari Ini", value: "42", sub: "dari 48 karyawan", icon: CheckCircle, accent: "#4ade80" },
            { label: "Tidak Hadir", value: "6", sub: "3 izin · 3 alpha", icon: XCircle, accent: "#f87171" },
            { label: "Terlambat", value: "5", sub: "dari yang hadir", icon: Clock, accent: "#FFB800" },
            { label: "Kehadiran", value: "87.5%", sub: "bulan ini", icon: TrendingUp, accent: "#60a5fa" },
          ].map((stat) => (
            <div
              key={stat.label}
              className="rounded-2xl p-4"
              style={{ background: "rgba(255,255,255,0.09)", border: "1px solid rgba(255,255,255,0.12)" }}
            >
              <div className="w-8 h-8 rounded-xl flex items-center justify-center mb-3" style={{ background: `${stat.accent}22` }}>
                <stat.icon className="w-4 h-4" style={{ color: stat.accent }} />
              </div>
              <div className="text-white" style={{ fontSize: "1.5rem", fontWeight: 800, lineHeight: 1 }}>{stat.value}</div>
              <div className="text-white/80 mt-1" style={{ fontSize: "0.75rem", fontWeight: 600 }}>{stat.label}</div>
              <div className="text-white/40 mt-0.5" style={{ fontSize: "0.65rem" }}>{stat.sub}</div>
            </div>
          ))}
        </div>

        {/* Scan CTA */}
        <button
          onClick={onScanPress}
          className="w-full relative overflow-hidden rounded-2xl p-5 flex items-center justify-between active:scale-95 transition-transform"
          style={{
            background: "linear-gradient(135deg, #FFB800 0%, #FF8C00 100%)",
            boxShadow: "0 8px 28px rgba(255,184,0,0.35)",
          }}
        >
          <div className="absolute inset-0" style={{ background: "linear-gradient(135deg, rgba(255,255,255,0.12) 0%, transparent 60%)" }} />
          <div>
            <div className="text-white" style={{ fontSize: "1rem", fontWeight: 700 }}>Scan Absensi</div>
            <div style={{ color: "rgba(255,255,255,0.8)", fontSize: "0.8rem", marginTop: "2px" }}>Gunakan face recognition</div>
          </div>
          <div className="w-14 h-14 rounded-2xl flex items-center justify-center flex-shrink-0" style={{ background: "rgba(0,0,0,0.15)" }}>
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M9 3H5a2 2 0 0 0-2 2v4" /><path d="M15 3h4a2 2 0 0 1 2 2v4" />
              <path d="M9 21H5a2 2 0 0 1-2-2v-4" /><path d="M15 21h4a2 2 0 0 0 2-2v-4" />
              <circle cx="12" cy="10" r="3" /><path d="M7 21c0-2.761 2.239-5 5-5s5 2.239 5 5" />
            </svg>
          </div>
        </button>

        {/* Recent Activity */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <h2 className="text-white" style={{ fontSize: "0.95rem", fontWeight: 700 }}>Aktivitas Terkini</h2>
            <button className="flex items-center gap-1" style={{ color: "#FFB800", fontSize: "0.8rem" }}>
              Lihat semua <ChevronRight className="w-3.5 h-3.5" />
            </button>
          </div>
          <div className="space-y-2.5">
            {recentActivity.map((item, i) => (
              <div
                key={i}
                className="flex items-center gap-3 p-3.5 rounded-xl"
                style={{ background: "rgba(255,255,255,0.08)", border: "1px solid rgba(255,255,255,0.1)" }}
              >
                <div className={`w-10 h-10 rounded-full bg-gradient-to-br ${avatarColors[i % avatarColors.length]} flex items-center justify-center flex-shrink-0`}>
                  <span className="text-white" style={{ fontSize: "0.7rem", fontWeight: 700 }}>{item.avatar}</span>
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-white" style={{ fontSize: "0.85rem", fontWeight: 600 }}>{item.name}</div>
                  <div className="text-white/50" style={{ fontSize: "0.75rem" }}>{item.action}</div>
                </div>
                <div className="text-right flex-shrink-0">
                  <div className="text-white" style={{ fontSize: "0.85rem", fontWeight: 600 }}>{item.time}</div>
                  <span
                    className="inline-block px-2 py-0.5 rounded-full mt-0.5"
                    style={{
                      fontSize: "0.65rem",
                      fontWeight: 600,
                      background: item.status === "on-time" ? "rgba(74,222,128,0.15)" : "rgba(255,184,0,0.15)",
                      color: item.status === "on-time" ? "#4ade80" : "#FFB800",
                    }}
                  >
                    {item.status === "on-time" ? "Tepat" : "Terlambat"}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Dept breakdown */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <h2 className="text-white" style={{ fontSize: "0.95rem", fontWeight: 700 }}>Per Departemen</h2>
            <button className="flex items-center gap-1" style={{ color: "#FFB800", fontSize: "0.8rem" }}>
              Detail <ChevronRight className="w-3.5 h-3.5" />
            </button>
          </div>
          <div className="rounded-2xl p-4 space-y-3" style={{ background: "rgba(255,255,255,0.07)", border: "1px solid rgba(255,255,255,0.1)" }}>
            {[
              { dept: "Engineering", hadir: 12, total: 14, pct: 85 },
              { dept: "Marketing", hadir: 8, total: 9, pct: 89 },
              { dept: "Finance", hadir: 10, total: 11, pct: 91 },
              { dept: "HR", hadir: 7, total: 7, pct: 100 },
              { dept: "Operations", hadir: 5, total: 7, pct: 71 },
            ].map((d) => (
              <div key={d.dept}>
                <div className="flex items-center justify-between mb-1.5">
                  <div className="flex items-center gap-2">
                    <Users className="w-3.5 h-3.5 text-white/40" />
                    <span className="text-white/80" style={{ fontSize: "0.8rem", fontWeight: 500 }}>{d.dept}</span>
                  </div>
                  <span className="text-white/50" style={{ fontSize: "0.75rem" }}>{d.hadir}/{d.total}</span>
                </div>
                <div className="h-1.5 rounded-full overflow-hidden" style={{ background: "rgba(255,255,255,0.1)" }}>
                  <div
                    className="h-full rounded-full transition-all"
                    style={{
                      width: `${d.pct}%`,
                      background: d.pct === 100 ? "#4ade80" : d.pct >= 85 ? "#FFB800" : "#f87171",
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
