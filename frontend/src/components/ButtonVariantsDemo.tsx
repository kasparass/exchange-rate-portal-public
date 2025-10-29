import { Button } from "@/components/ui/button"

export function ButtonVariantsDemo() {
  const variants = [
    'default',
    'destructive',
    'outline',
    'secondary',
    'ghost',
    'link',
  ] as const

  const sizes = [
    'default',
    'sm',
    'lg',
    'icon',
  ] as const

  return (
    <div className="space-y-8 p-6">
      <div>
        <h2 className="text-2xl font-bold mb-4">Button Variants</h2>
        <div className="flex flex-wrap gap-4">
          {variants.map((variant) => (
            <div key={variant} className="flex flex-col items-center gap-2">
              <Button variant={variant}>
                {variant.charAt(0).toUpperCase() + variant.slice(1)}
              </Button>
              <span className="text-sm text-muted-foreground">{variant}</span>
            </div>
          ))}
        </div>
      </div>

      <div>
        <h2 className="text-2xl font-bold mb-4">Button Sizes</h2>
        <div className="flex flex-wrap items-center gap-4">
          {sizes.map((size) => (
            <div key={size} className="flex flex-col items-center gap-2">
              <Button size={size}>
                {size === 'icon' ? '🔍' : `Size: ${size}`}
              </Button>
              <span className="text-sm text-muted-foreground">{size}</span>
            </div>
          ))}
        </div>
      </div>

      <div>
        <h2 className="text-2xl font-bold mb-4">Button States</h2>
        <div className="flex flex-wrap gap-4">
          <div className="flex flex-col items-center gap-2">
            <Button disabled>Disabled</Button>
            <span className="text-sm text-muted-foreground">disabled</span>
          </div>
          <div className="flex flex-col items-center gap-2">
            <Button isLoading>Loading</Button>
            <span className="text-sm text-muted-foreground">isLoading</span>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ButtonVariantsDemo
