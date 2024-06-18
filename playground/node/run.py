import nodriver as uc
import asyncio

async def main():
    # Start the browser
    browser = await uc.start()
    # Visit the target page
    URL = 'http://localhost:3000'
    await browser.get(URL)
    
    try:
        # Keep the browser open until manually closed by the user
        while True:
            await asyncio.sleep(1)
    except KeyboardInterrupt:
        # Close the browser when interrupted
        await browser.close()

# Run the main function
if __name__ == '__main__':
    uc.loop().run_until_complete(main())

