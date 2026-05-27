import './styles.css'

const features = [
  {
    title: 'AI Chat',
    description: 'Multi-agent diagnosis, estimation, and safety guidance tailored for automotive workflows.'
  },
  {
    title: 'Repair Estimator',
    description: 'Structured 4-step estimate flow with VIN decode and itemized recommendations.'
  },
  {
    title: 'DTC Lookup',
    description: 'Search and analyze trouble codes with likely causes and confidence guidance.'
  },
  {
    title: 'Fleet Dashboard',
    description: 'Track vehicle costs, maintenance trends, and fleet-level efficiency.'
  }
]

const root = document.querySelector('#app')

root.innerHTML = `
  <div class="page">
    <header class="hero">
      <p class="eyebrow">AA Car Repair Android AI</p>
      <h1>Production Android frontend, now properly wired.</h1>
      <p class="subtitle">
        This site is now connected to the actual frontend entry point and mirrors the real product capabilities
        from the Android application.
      </p>
      <div class="cta-row">
        <a class="btn btn-primary" href="./README.md">View project docs</a>
        <a class="btn" href="./ARCHITECTURE.md">Architecture</a>
      </div>
    </header>

    <section>
      <h2>Core frontend experiences</h2>
      <div class="grid" id="feature-grid"></div>
    </section>
  </div>
`

const grid = document.querySelector('#feature-grid')
features.forEach((feature) => {
  const card = document.createElement('article')
  card.className = 'card'
  card.innerHTML = `<h3>${feature.title}</h3><p>${feature.description}</p>`
  grid.appendChild(card)
})
