import { useState } from "react";
import { Search, Plus, Filter, ChevronRight, Phone, Mail, MoreVertical } from "lucide-react";

const employees = [
  { id: "EMP-001", name: "Budi Santoso", dept: "Engineering", role: "Senior Developer", status: "hadir", avatar: "BS", color: "from-yellow-400 to-orange-400", phone: "+62 812-3456-7890", email: "budi@talangmas.co.id", joined: "2021-03" },
  { id: "EMP-002", name: "Sari Dewi", dept: "Marketing", role: "Marketing Manager", status: "hadir", avatar: "SD", color: "from-pink-300 to-rose-400", phone: "+62 813-2345-6789", email: "sari@talangmas.co.id", joined: "2020-07" },
  { id: "EMP-003", name: "Andi Pratama", dept: "Finance", role: "Financial Analyst", status: "terlambat", avatar: "AP", color: "from-emerald-400 to-teal-400", phone: "+62 814-3456-7891", email: "andi@talangmas.co.id", joined: "2022-01" },
  { id: "EMP-004", name: "Rina Wahyu", dept: "HR", role: "HR Specialist", status: "hadir", avatar: "RW", color: "from-blue-300 to-sky-400", phone: "+62 815-4567-8901", email: "rina@talangmas.co.id", joined: "2019-11" },
  { id: "EMP-005", name: "Doni Kusuma", dept: "Operations", role: "Ops Coordinator", status: "izin", avatar: "DK", color: "from-orange-300 to-amber-400", phone: "+62 816-5678-9012", email: "doni@talangmas.co.id", joined: "2023-04" },
  { id: "EMP-006", name: "Maya Lestari", dept: "Engineering", role: "UI/UX Designer", status: "hadir", avatar: "ML", color: "from-violet-400 to-indigo-400", phone: "+62 817-6789-0123", email: "maya@talangmas.co.id", joined: "2022-09" },
  { id: "EMP-007", name: "Fajar Rahman", dept: "Marketing", role: "Content Creator", status: "alpha", avatar: "FR", color: "from-cyan-300 to-blue-400", phone: "+62 818-7890-1234", email: "fajar@talangmas.co.id", joined: "2023-06" },
  { id: "EMP-008", name: "Lina Suryani", dept: "Finance", role: "Accountant", status: "hadir", avatar: "LS", color: "from-teal-300 to-emerald-400", phone: "+62 819-8901-2345", email: "lina@talangmas.co.id", joined: "2021-08" },
];

const depts = ["Semua", "Engineering", "Marketing", "Finance", "HR", "Operations"];

const statusConfig = {
  hadir: { label: "Hadir", bg: "rgba(74,222,128,0.15)", color: "#4ade80", border: "rgba(74,222,128,0.3)" },
  terlambat: { label: "Terlambat", bg: "rgba(255,184,0,0.15)", color: "#FFB800", border: "rgba(255,184,0,0.3)" },
  izin: { label: "Izin", bg: "rgba(96,165,250,0.15)", color: "#60a5fa", border: "rgba(96,165,250,0.3)" },
  alpha: { label: "Alpha", bg: "rgba(248,113,113,0.15)", color: "#f87171", border: "rgba(248,113,113,0.3)" },
};

export function PeopleScreen() {
  const [search, setSearch] = useState("");
  const [activeDept, setActiveDept] = useState("Semua");
  const [expanded, setExpanded] = useState<string | null>(null);

  const filtered = employees.filter((e) => {
    const matchSearch = e.name.toLowerCase().includes(search.toLowerCase()) || e.dept.toLowerCase().includes(search.toLowerCase());
    const matchDept = activeDept === "Semua" || e.dept === activeDept;
    return matchSearch && matchDept;
  });

  return (
    <div className="flex flex-col h-full overflow-hidden" style={{ background: "#003399" }}>
      {/* Header */}
      <div
        className="px-5 pt-4 pb-4"
        style={{ background: "linear-gradient(180deg, #001f6e 0%, #003399 100%)" }}
      >
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-white" style={{ fontSize: "1.2rem", fontWeight: 700 }}>Karyawan</h1>
            <p className="text-white/50" style={{ fontSize: "0.8rem" }}>{employees.length} orang terdaftar</p>
          </div>
          <button
            className="w-9 h-9 rounded-full flex items-center justify-center shadow-lg"
            style={{ background: "linear-gradient(135deg, #FFB800, #FF8C00)", boxShadow: "0 4px 14px rgba(255,184,0,0.4)" }}
          >
            <Plus className="w-5 h-5 text-white" />
          </button>
        </div>

        {/* Search */}
        <div className="relative mb-3">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-white/30" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Cari nama atau departemen..."
            className="w-full rounded-xl pl-10 pr-10 py-3 text-white placeholder-white/30 outline-none transition-all"
            style={{
              background: "rgba(255,255,255,0.1)",
              border: "1px solid rgba(255,255,255,0.15)",
              fontSize: "0.875rem",
            }}
          />
          <button className="absolute right-3 top-1/2 -translate-y-1/2 w-7 h-7 rounded-lg flex items-center justify-center" style={{ background: "rgba(255,255,255,0.1)" }}>
            <Filter className="w-3.5 h-3.5 text-white/50" />
          </button>
        </div>

        {/* Dept tabs */}
        <div className="flex gap-2 overflow-x-auto pb-1">
          {depts.map((dept) => (
            <button
              key={dept}
              onClick={() => setActiveDept(dept)}
              className="flex-shrink-0 px-3 py-1.5 rounded-full transition-all"
              style={{
                background: activeDept === dept ? "linear-gradient(135deg, #FFB800, #FF8C00)" : "rgba(255,255,255,0.1)",
                border: activeDept === dept ? "none" : "1px solid rgba(255,255,255,0.12)",
                color: activeDept === dept ? "white" : "rgba(255,255,255,0.5)",
                fontSize: "0.75rem",
                fontWeight: 600,
                boxShadow: activeDept === dept ? "0 3px 10px rgba(255,184,0,0.3)" : "none",
              }}
            >
              {dept}
            </button>
          ))}
        </div>
      </div>

      {/* List */}
      <div className="flex-1 overflow-y-auto px-5 pb-8 pt-3 space-y-2.5">
        {filtered.map((emp) => {
          const isOpen = expanded === emp.id;
          const sc = statusConfig[emp.status as keyof typeof statusConfig];

          return (
            <div
              key={emp.id}
              className="rounded-2xl overflow-hidden transition-all"
              style={{ background: "rgba(255,255,255,0.07)", border: "1px solid rgba(255,255,255,0.1)" }}
            >
              <button
                className="w-full flex items-center gap-3 p-4 text-left transition-colors active:opacity-80"
                onClick={() => setExpanded(isOpen ? null : emp.id)}
              >
                <div className={`w-11 h-11 rounded-xl bg-gradient-to-br ${emp.color} flex items-center justify-center flex-shrink-0 shadow-lg`}>
                  <span className="text-white" style={{ fontSize: "0.75rem", fontWeight: 700 }}>{emp.avatar}</span>
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-white" style={{ fontSize: "0.875rem", fontWeight: 600 }}>{emp.name}</div>
                  <div className="text-white/50 truncate" style={{ fontSize: "0.75rem" }}>{emp.role} · {emp.dept}</div>
                </div>
                <div className="flex items-center gap-2 flex-shrink-0">
                  <span
                    className="px-2 py-0.5 rounded-full"
                    style={{ fontSize: "0.65rem", fontWeight: 600, background: sc.bg, color: sc.color, border: `1px solid ${sc.border}` }}
                  >
                    {sc.label}
                  </span>
                  <ChevronRight className={`w-4 h-4 text-white/30 transition-transform ${isOpen ? "rotate-90" : ""}`} />
                </div>
              </button>

              {isOpen && (
                <div className="px-4 pb-4 border-t pt-3" style={{ borderColor: "rgba(255,255,255,0.08)" }}>
                  <div className="grid grid-cols-2 gap-2 mb-3">
                    {[
                      { label: "ID Karyawan", val: emp.id },
                      { label: "Departemen", val: emp.dept },
                      { label: "Bergabung", val: emp.joined },
                      { label: "Status", val: sc.label },
                    ].map((info) => (
                      <div key={info.label} className="rounded-xl p-2.5" style={{ background: "rgba(255,255,255,0.08)" }}>
                        <div className="text-white/35 uppercase tracking-wider" style={{ fontSize: "0.6rem", fontWeight: 600 }}>{info.label}</div>
                        <div className="text-white mt-0.5" style={{ fontSize: "0.8rem", fontWeight: 600 }}>{info.val}</div>
                      </div>
                    ))}
                  </div>
                  <div className="flex gap-2">
                    <a
                      href={`tel:${emp.phone}`}
                      className="flex-1 flex items-center justify-center gap-2 rounded-xl py-2.5 transition-opacity active:opacity-70"
                      style={{ background: "rgba(255,184,0,0.15)", border: "1px solid rgba(255,184,0,0.25)", color: "#FFB800" }}
                    >
                      <Phone className="w-3.5 h-3.5" />
                      <span style={{ fontSize: "0.8rem", fontWeight: 600 }}>Telepon</span>
                    </a>
                    <a
                      href={`mailto:${emp.email}`}
                      className="flex-1 flex items-center justify-center gap-2 rounded-xl py-2.5 transition-opacity active:opacity-70"
                      style={{ background: "rgba(255,255,255,0.08)", border: "1px solid rgba(255,255,255,0.12)", color: "rgba(255,255,255,0.6)" }}
                    >
                      <Mail className="w-3.5 h-3.5" />
                      <span style={{ fontSize: "0.8rem", fontWeight: 600 }}>Email</span>
                    </a>
                    <button
                      className="w-10 h-10 flex items-center justify-center rounded-xl"
                      style={{ background: "rgba(255,255,255,0.08)", border: "1px solid rgba(255,255,255,0.12)", color: "rgba(255,255,255,0.5)" }}
                    >
                      <MoreVertical className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              )}
            </div>
          );
        })}

        {filtered.length === 0 && (
          <div className="flex flex-col items-center justify-center py-16">
            <div className="w-16 h-16 rounded-full flex items-center justify-center mb-3" style={{ background: "rgba(255,255,255,0.08)" }}>
              <Search className="w-7 h-7 text-white/30" />
            </div>
            <p className="text-white/50" style={{ fontSize: "0.9rem" }}>Tidak ada karyawan ditemukan</p>
          </div>
        )}
      </div>
    </div>
  );
}
